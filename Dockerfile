FROM hseeberger/scala-sbt
MAINTAINER "Bryce McDonnell <bryce@bridgetownint.com">

RUN mkdir /app
ADD . /app

WORKDIR /app

# this is the command to run in production
CMD ["sbt", "-Xms512M", "-Xmx1024M", "-Xss1M", "-XX:+CMSClassUnloadingEnabled", "run"]