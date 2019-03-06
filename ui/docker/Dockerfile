FROM node:lts-alpine AS ngBuild

COPY ./e2e/ /opt/cleanPath/e2e/
COPY ./src/ /opt/cleanPath/src/
COPY ./*.json /opt/cleanPath/

WORKDIR /opt/cleanPath

RUN npm install -g @angular/cli@~7.3.3
RUN npm install
RUN ng build --aot=true --prod=true

FROM nginx:latest

COPY --from=ngBuild /opt/cleanPath/dist/cleanPath/ /opt/cleanPath/
COPY ./nginx.config /etc/nginx/conf.d/default.conf

EXPOSE 80