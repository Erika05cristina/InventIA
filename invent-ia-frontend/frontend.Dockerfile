FROM node:22 AS build
WORKDIR /usr/src/app
COPY package*.json ./
RUN npm install
RUN ng update @angular/core@20 @angular/cli@20
COPY . .

RUN npm run build -- --configuration production

FROM nginx:stable-alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=build /usr/src/app/dist/invent-ia-frontend/browser /usr/share/nginx/html

EXPOSE 80
