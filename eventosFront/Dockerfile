FROM node:18 AS build

WORKDIR /app

ENV NODE_OPTIONS=--max_old_space_size=2048

COPY package*.json ./
RUN npm install --legacy-peer-deps

COPY . .

RUN npm run build

FROM nginx:alpine

COPY --from=build /app/www /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]