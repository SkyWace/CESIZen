# Cesizen Backoffice

A Vue.js-based backoffice for managing the Cesizen application.

## Features

- User authentication
- User management (view, edit, delete)
- Modern UI with Vuetify
- Responsive design

## Prerequisites

- Node.js (v16 or higher)
- npm (v7 or higher)

## Setup

1. Install dependencies:
```bash
npm install
```

2. Create a `.env` file in the root directory with the following content:
```
VITE_API_URL=http://localhost:8080/api
```

3. Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

## Building for Production

To build the application for production:

```bash
npm run build
```

The built files will be in the `dist` directory.

## Project Structure

- `src/views/` - Main view components
- `src/router/` - Vue Router configuration
- `src/components/` - Reusable components
- `src/stores/` - Pinia stores for state management

## API Integration

The backoffice integrates with the Java backend API. Make sure the backend server is running on `http://localhost:8080` before using the backoffice.
