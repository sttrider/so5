# node builder
FROM node:14.19.3-alpine3.15 AS builder
WORKDIR /so5
COPY package.json .
COPY src ./src
COPY public ./public
RUN npm install --silent
RUN npm run build

# nginx
FROM nginx:1.21.6-alpine
COPY --from=builder so5/build /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf