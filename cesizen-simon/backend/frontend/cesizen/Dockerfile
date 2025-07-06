# Stage 1: Build
FROM node:18 AS build-stage

# Set working directory
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy rest of the application code
COPY . .

# Build the Vue app for production
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine AS production-stage

# Copy built files to Nginx web directory
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Copy custom Nginx configuration (optional)
# COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
