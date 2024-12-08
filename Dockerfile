FROM node:20

WORKDIR /app
ENV PORT 8000
ENV HOST 0.0.0.0

COPY . .
RUN npm install
EXPOSE 8000
CMD [ "npm", "run", "start"]