# Use a specific version of the official Node.js image as the build stage
FROM node:21.7.3-bullseye AS build

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json to install dependencies efficiently and leverage layer caching
COPY package*.json ./

# Set up npm cache in a designated directory to improve caching
RUN --mount=type=cache,target=/usr/src/app/.npm \
    npm set cache /usr/src/app/.npm && \
    npm install

# Copy the entire application source code
COPY . .

# Run the build command to generate production-ready artifacts
RUN npm run build

# Use Nginx for serving the built application
FROM nginx:1.21.1-alpine

# Copy the build output to replace the default Nginx contents.
COPY --from=build /usr/src/app/build /usr/share/nginx/html

RUN echo "server { listen 80; location / { root /usr/share/nginx/html; try_files \$uri /index.html; } }" > /etc/nginx/conf.d/default.conf

EXPOSE 80