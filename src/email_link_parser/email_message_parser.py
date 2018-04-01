"""
This utility parses received email messages and finds links

This is definitely a WIP in this state, but I want something simple that can
completely and simply parse an email message and extract all the links.
"""

import email
import time
import psycopg2
import os
from bs4 import BeautifulSoup

def parse_emails_for_links():
    conn = psycopg2.connect(host=os.getenv("DB_HOST"),
                            user=os.getenv("DB_USERNAME"),
                            password=os.getenv("DB_PASSWORD"),
                            database=os.getenv("DB_NAME"))

    with conn as db:
        with db.cursor() as curs:
            curs.execute("""
                SELECT
                ses_message_json
                , ses_message_id
                FROM emails_received
                WHERE NOT parsed""")
            messages = curs.fetchall()

    for message,id in messages:

        msg = email.message_from_string(message['content'])

        html_doc = "<html></html>"
        for part in msg.walk():
            if part.get_content_type() == 'text/html':
                html_doc = part.get_payload(decode=True)

        soup = BeautifulSoup(html_doc, 'html.parser')

        for link in soup.find_all('a'):
            print("link: %s" % link)
            href = link.get('href')
            text = link.get_text()
            print("href: %s" % href)
            print("text: %s" % text)

            # NOTE: sometimes the text is an embedded image or some other artifact that doesn't make sense
            if text == '':
                continue

            with conn as db:
                with db.cursor() as curs:
                    curs.execute("""
                        INSERT INTO email_links
                        (emails_received_ses_message_id, cta, href) VALUES (%s, %s, %s)
                        ON CONFLICT(href)
                        DO NOTHING
                    """, (id, text, href))

        with conn as db:
            with db.cursor() as curs:
                curs.execute("""
                    UPDATE emails_received
                    SET parsed = TRUE
                    WHERE ses_message_id = %s
                """, (id, ))

if __name__ == "__main__":
    while True:
        print("checking")
        parse_emails_for_links()
        time.sleep(5)
