FROM python:3.6

MAINTAINER Bryce McDonnell <bryce@bridgetownint.com>

RUN mkdir /app
WORKDIR /app
ADD email_message_parser.py /app

ADD requirements.txt /app

RUN pip install --upgrade pip
RUN pip install --trusted-host pypi.python.org -r requirements.txt

CMD python /app/email_message_parser.py