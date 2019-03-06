FROM node:lts-alpine

COPY ./*.js /opt/cleanPath/
COPY ./package.json /opt/cleanPath/

WORKDIR /opt/cleanPath

RUN npm install

# 8081 telemetry web socket
# 8082 api port
EXPOSE 8081 8082

ENTRYPOINT [ "node" ]
CMD [ "index.js" ]