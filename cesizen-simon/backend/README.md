# CESIZen - Application de Santé Mentale et Exercices de Respiration

Ce projet est composé de plusieurs modules qui peuvent être installés et démarrés séparément.

## Prérequis

- Java 17
- Node.js >=20.0.0 (requis pour Capacitor CLI)
- Docker et Docker Compose
- PostgreSQL 13 (via Docker)
- Android Studio (pour l'application mobile)
- Xcode (pour l'application mobile iOS)

## Installation et Démarrage

### 1. Backend (Spring Boot 3.4.5)

Le backend est une application Spring Boot qui gère l'API et la base de données.

```bash
# Se placer dans le dossier backend
cd backend

# Installer les dépendances Maven
./mvnw clean install

# Démarrer l'application
./mvnw spring-boot:run
```

Le serveur démarrera sur `http://localhost:8080`

### 2. Base de données (PostgreSQL 13)

La base de données est configurée pour démarrer via Docker Compose :

```bash
# À la racine du projet
docker-compose up -d
```

La base de données sera accessible sur `localhost:5432` avec les identifiants suivants :
- Base de données : cesizen
- Utilisateur : postgres
- Mot de passe : postgres

### 3. Backoffice (Vue.js avec Pinia)

Le backoffice est une application Vue.js pour la gestion administrative.

```bash
# Se placer dans le dossier backoffice
cd backoffice

# Installer les dépendances
npm install

# Démarrer l'application en mode développement
npm run dev
```

L'application sera accessible sur `http://localhost:3000`

### 4. Frontend (Vue.js)

Le frontend est l'interface utilisateur principale de l'application.

```bash
# Se placer dans le dossier frontend/cesizen
cd frontend/cesizen

# Installer les dépendances
npm install

# Démarrer l'application en mode développement
npm run dev
```

L'application sera accessible sur `http://localhost:3001`

### 5. Application Mobile (Ionic/Capacitor)

L'application mobile est développée avec Ionic et Capacitor, basée sur Vue.js.

```bash
# Se placer dans le dossier mobile-app
cd mobile-app

# Installer les dépendances
npm install

# Démarrer l'application en mode développement
npm run dev

# Pour construire l'application
npm run build

# Pour iOS
npx cap add ios
npx cap sync ios
npx cap open ios

# Pour Android
npx cap add android
npx cap sync android
npx cap open android
```

## Documentation API

La documentation de l'API est disponible à l'adresse `http://localhost:8080/swagger-ui.html` lorsque le backend est en cours d'exécution.

## Support

Pour toute question ou problème, veuillez créer une issue dans le repository GitHub du projet. 
