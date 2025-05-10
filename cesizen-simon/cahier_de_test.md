# Cahier de Test - CESIZen

## Table des matières
1. [Introduction](#introduction)
2. [Tests Backend](#tests-backend)
   - [Tests des API](#tests-des-api)
   - [Tests des Services](#tests-des-services)
   - [Tests des Repositories](#tests-des-repositories)
   - [Tests de Sécurité](#tests-de-sécurité)
3. [Tests Frontend](#tests-frontend)
   - [Tests d'Interface Utilisateur](#tests-dinterface-utilisateur)
   - [Tests de Fonctionnalités](#tests-de-fonctionnalités)
4. [Tests Mobile](#tests-mobile)
   - [Tests sur Android](#tests-sur-android)
   - [Tests sur iOS](#tests-sur-ios)
5. [Tests d'Intégration](#tests-dintégration)
6. [Tests de Performance](#tests-de-performance)
7. [Tests de Sécurité](#tests-de-sécurité-globaux)

## Introduction

Ce cahier de test documente les procédures de test pour l'application CESIZen, une application de santé mentale et d'exercices de respiration. L'application est composée de plusieurs modules:
- Backend (Spring Boot)
- Frontend (Vue.js)
- Backoffice (Vue.js avec Pinia)
- Application Mobile (Ionic/Capacitor)

## Tests Backend

### Tests des API

#### Test de l'API d'Authentification

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| AUTH-01 | Inscription utilisateur | 1. Envoyer une requête POST à `/api/auth/signup` avec des informations valides | Réponse 200 OK avec message de succès | ✅ |
| AUTH-02 | Connexion utilisateur | 1. Envoyer une requête POST à `/api/auth/signin` avec des identifiants valides | Réponse 200 OK avec token JWT | ✅ |
| AUTH-03 | Tentative de connexion avec identifiants invalides | 1. Envoyer une requête POST à `/api/auth/signin` avec des identifiants invalides | Réponse 401 Unauthorized | ✅ |
| AUTH-04 | Déconnexion utilisateur | 1. Envoyer une requête POST à `/api/auth/signout` avec un token valide | Réponse 200 OK avec message de succès | ✅ |

#### Test de l'API des Articles

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| ART-01 | Récupération de tous les articles | 1. Envoyer une requête GET à `/api/articles` | Réponse 200 OK avec liste d'articles | ✅ |
| ART-02 | Récupération d'un article par ID | 1. Envoyer une requête GET à `/api/articles/{id}` avec un ID valide | Réponse 200 OK avec détails de l'article | ✅ |
| ART-03 | Création d'un nouvel article | 1. Envoyer une requête POST à `/api/articles` avec des données valides et un token admin | Réponse 201 Created avec l'article créé | ✅ |
| ART-04 | Mise à jour d'un article | 1. Envoyer une requête PUT à `/api/articles/{id}` avec des données valides et un token admin | Réponse 200 OK avec l'article mis à jour | ✅ |
| ART-05 | Suppression d'un article | 1. Envoyer une requête DELETE à `/api/articles/{id}` avec un token admin | Réponse 204 No Content | ✅ |

#### Test de l'API des Exercices de Respiration

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| BREATH-01 | Récupération de tous les exercices | 1. Envoyer une requête GET à `/api/breathing-exercises` | Réponse 200 OK avec liste d'exercices | ✅ |
| BREATH-02 | Récupération d'un exercice par ID | 1. Envoyer une requête GET à `/api/breathing-exercises/{id}` avec un ID valide | Réponse 200 OK avec détails de l'exercice | ✅ |
| BREATH-03 | Création d'un nouvel exercice | 1. Envoyer une requête POST à `/api/breathing-exercises` avec des données valides et un token admin | Réponse 201 Created avec l'exercice créé | ✅ |
| BREATH-04 | Mise à jour d'un exercice | 1. Envoyer une requête PUT à `/api/breathing-exercises/{id}` avec des données valides et un token admin | Réponse 200 OK avec l'exercice mis à jour | ✅ |
| BREATH-05 | Suppression d'un exercice | 1. Envoyer une requête DELETE à `/api/breathing-exercises/{id}` avec un token admin | Réponse 204 No Content | ✅ |

#### Test de l'API d'Historique des Exercices

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| HIST-01 | Récupération de l'historique d'un utilisateur | 1. Envoyer une requête GET à `/api/breathing-history/user/{userId}` avec un token valide | Réponse 200 OK avec historique | ✅ |
| HIST-02 | Ajout d'un exercice à l'historique | 1. Envoyer une requête POST à `/api/breathing-history` avec des données valides et un token valide | Réponse 201 Created avec l'entrée d'historique créée | ✅ |

#### Test de l'API des Feedbacks d'Exercices

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| FEED-01 | Soumission d'un feedback | 1. Envoyer une requête POST à `/api/feedback` avec des données valides et un token valide | Réponse 201 Created avec le feedback créé | ✅ |
| FEED-02 | Récupération des feedbacks pour un exercice | 1. Envoyer une requête GET à `/api/feedback/exercise/{exerciseId}` avec un token admin | Réponse 200 OK avec liste de feedbacks | ✅ |

#### Test de l'API de Modération des Feedbacks

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| MOD-01 | Approbation d'un feedback | 1. Envoyer une requête PUT à `/api/moderation/feedback/{id}/approve` avec un token admin | Réponse 200 OK avec feedback approuvé | ✅ |
| MOD-02 | Rejet d'un feedback | 1. Envoyer une requête PUT à `/api/moderation/feedback/{id}/reject` avec un token admin | Réponse 200 OK avec feedback rejeté | ✅ |

#### Test de l'API des Informations de Santé Mentale

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| INFO-01 | Récupération de toutes les informations | 1. Envoyer une requête GET à `/api/mental-health-info` | Réponse 200 OK avec liste d'informations | ✅ |
| INFO-02 | Récupération d'une information par ID | 1. Envoyer une requête GET à `/api/mental-health-info/{id}` avec un ID valide | Réponse 200 OK avec détails de l'information | ✅ |

#### Test de l'API des Utilisateurs

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| USER-01 | Récupération du profil utilisateur | 1. Envoyer une requête GET à `/api/users/profile` avec un token valide | Réponse 200 OK avec profil utilisateur | ✅ |
| USER-02 | Mise à jour du profil utilisateur | 1. Envoyer une requête PUT à `/api/users/profile` avec des données valides et un token valide | Réponse 200 OK avec profil mis à jour | ✅ |
| USER-03 | Récupération de tous les utilisateurs | 1. Envoyer une requête GET à `/api/users` avec un token admin | Réponse 200 OK avec liste d'utilisateurs | ✅ |

### Tests des Services

#### Test du Service d'Authentification

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| SVC-AUTH-01 | Inscription avec email déjà utilisé | 1. Appeler la méthode d'inscription avec un email déjà existant | Exception levée avec message approprié | ✅ |
| SVC-AUTH-02 | Validation des rôles lors de l'inscription | 1. Appeler la méthode d'inscription avec des rôles invalides | Exception levée avec message approprié | ✅ |

#### Test du Service des Articles

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| SVC-ART-01 | Récupération d'un article inexistant | 1. Appeler la méthode getArticleById avec un ID inexistant | Exception ResourceNotFoundException levée | ✅ |
| SVC-ART-02 | Mise à jour d'un article inexistant | 1. Appeler la méthode updateArticle avec un ID inexistant | Exception ResourceNotFoundException levée | ✅ |

### Tests des Repositories

#### Test du Repository des Utilisateurs

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| REPO-USER-01 | Recherche d'utilisateur par email | 1. Appeler la méthode findByEmail avec un email existant | Utilisateur correspondant retourné | ✅ |
| REPO-USER-02 | Vérification d'existence d'email | 1. Appeler la méthode existsByEmail avec un email existant | true retourné | ✅ |

#### Test du Repository des Articles

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| REPO-ART-01 | Recherche d'articles par catégorie | 1. Appeler la méthode findByCategory avec une catégorie existante | Liste d'articles correspondants retournée | ✅ |
| REPO-ART-02 | Recherche d'articles par mot-clé dans le titre | 1. Appeler la méthode findByTitleContaining avec un mot-clé | Liste d'articles correspondants retournée | ✅ |

### Tests de Sécurité

#### Test des JWT

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| SEC-JWT-01 | Validation d'un token JWT valide | 1. Générer un token JWT valide<br>2. Valider le token | Token validé avec succès | ✅ |
| SEC-JWT-02 | Validation d'un token JWT expiré | 1. Générer un token JWT expiré<br>2. Tenter de valider le token | Exception levée pour token expiré | ✅ |
| SEC-JWT-03 | Validation d'un token JWT altéré | 1. Générer un token JWT<br>2. Altérer le token<br>3. Tenter de valider le token | Exception levée pour signature invalide | ✅ |

#### Test des Autorisations

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| SEC-AUTH-01 | Accès à une ressource protégée avec un rôle valide | 1. Générer un token avec le rôle approprié<br>2. Accéder à une ressource protégée | Accès autorisé | ✅ |
| SEC-AUTH-02 | Accès à une ressource protégée avec un rôle invalide | 1. Générer un token avec un rôle insuffisant<br>2. Tenter d'accéder à une ressource protégée | Accès refusé (403 Forbidden) | ✅ |
| SEC-AUTH-03 | Accès à une ressource protégée sans token | 1. Tenter d'accéder à une ressource protégée sans token | Accès refusé (401 Unauthorized) | ✅ |

## Tests Frontend

### Tests d'Interface Utilisateur

#### Test de la Page d'Accueil

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| UI-HOME-01 | Chargement de la page d'accueil | 1. Accéder à l'URL de base de l'application | Page d'accueil affichée correctement avec tous les éléments | ✅ |
| UI-HOME-02 | Navigation vers les différentes sections | 1. Cliquer sur les liens de navigation | Navigation correcte vers les sections correspondantes | ✅ |

#### Test de la Page d'Authentification

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| UI-AUTH-01 | Affichage du formulaire de connexion | 1. Accéder à la page de connexion | Formulaire de connexion affiché correctement | ✅ |
| UI-AUTH-02 | Affichage du formulaire d'inscription | 1. Accéder à la page d'inscription | Formulaire d'inscription affiché correctement | ✅ |
| UI-AUTH-03 | Validation des champs du formulaire | 1. Soumettre un formulaire avec des champs invalides | Messages d'erreur appropriés affichés | ✅ |

#### Test de la Page des Exercices de Respiration

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| UI-BREATH-01 | Affichage de la liste des exercices | 1. Accéder à la page des exercices | Liste des exercices affichée correctement | ✅ |
| UI-BREATH-02 | Affichage des détails d'un exercice | 1. Cliquer sur un exercice | Détails de l'exercice affichés correctement | ✅ |
| UI-BREATH-03 | Fonctionnement de l'animation de respiration | 1. Démarrer un exercice de respiration | Animation de respiration fonctionne correctement | ✅ |

### Tests de Fonctionnalités

#### Test de l'Authentification

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| FUNC-AUTH-01 | Inscription réussie | 1. Remplir le formulaire d'inscription avec des données valides<br>2. Soumettre le formulaire | Inscription réussie et redirection vers la page de connexion | ✅ |
| FUNC-AUTH-02 | Connexion réussie | 1. Remplir le formulaire de connexion avec des identifiants valides<br>2. Soumettre le formulaire | Connexion réussie et redirection vers la page d'accueil | ✅ |
| FUNC-AUTH-03 | Déconnexion | 1. Cliquer sur le bouton de déconnexion | Déconnexion réussie et redirection vers la page d'accueil | ✅ |

#### Test de la Gestion des Articles

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| FUNC-ART-01 | Affichage des articles | 1. Accéder à la page des articles | Articles affichés correctement | ✅ |
| FUNC-ART-02 | Recherche d'articles | 1. Entrer un terme de recherche<br>2. Soumettre la recherche | Articles correspondants affichés | ✅ |
| FUNC-ART-03 | Filtrage des articles par catégorie | 1. Sélectionner une catégorie | Articles de la catégorie sélectionnée affichés | ✅ |

#### Test des Exercices de Respiration

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| FUNC-BREATH-01 | Démarrage d'un exercice | 1. Sélectionner un exercice<br>2. Cliquer sur "Démarrer" | Exercice démarré avec animation | ✅ |
| FUNC-BREATH-02 | Pause d'un exercice | 1. Pendant un exercice, cliquer sur "Pause" | Exercice mis en pause | ✅ |
| FUNC-BREATH-03 | Reprise d'un exercice | 1. Pendant un exercice en pause, cliquer sur "Reprendre" | Exercice repris | ✅ |
| FUNC-BREATH-04 | Arrêt d'un exercice | 1. Pendant un exercice, cliquer sur "Arrêter" | Exercice arrêté et retour à la liste | ✅ |
| FUNC-BREATH-05 | Enregistrement d'un exercice terminé | 1. Terminer un exercice<br>2. Confirmer l'enregistrement | Exercice enregistré dans l'historique | ✅ |

#### Test de l'Historique des Exercices

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| FUNC-HIST-01 | Affichage de l'historique | 1. Accéder à la page d'historique | Historique affiché correctement | ✅ |
| FUNC-HIST-02 | Filtrage de l'historique par date | 1. Sélectionner une plage de dates<br>2. Appliquer le filtre | Historique filtré correctement | ✅ |
| FUNC-HIST-03 | Filtrage de l'historique par type d'exercice | 1. Sélectionner un type d'exercice<br>2. Appliquer le filtre | Historique filtré correctement | ✅ |

## Tests Mobile

### Tests sur Android

#### Test de l'Interface Utilisateur

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| MOB-AND-UI-01 | Affichage sur différentes tailles d'écran | 1. Tester l'application sur différents appareils Android (petit, moyen, grand écran) | Interface adaptée correctement à chaque taille d'écran | ✅ |
| MOB-AND-UI-02 | Orientation portrait/paysage | 1. Changer l'orientation de l'appareil | Interface adaptée correctement à l'orientation | ✅ |

#### Test des Fonctionnalités Spécifiques à Android

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| MOB-AND-FUNC-01 | Notifications | 1. Déclencher une notification<br>2. Vérifier la réception | Notification reçue correctement | ✅ |
| MOB-AND-FUNC-02 | Stockage local | 1. Enregistrer des données localement<br>2. Fermer l'application<br>3. Rouvrir l'application | Données persistantes correctement | ✅ |
| MOB-AND-FUNC-03 | Permissions | 1. Demander une permission (ex: notifications)<br>2. Accepter/refuser la permission | Application gère correctement la réponse | ✅ |

### Tests sur iOS

#### Test de l'Interface Utilisateur

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| MOB-IOS-UI-01 | Affichage sur différents modèles d'iPhone | 1. Tester l'application sur différents modèles d'iPhone | Interface adaptée correctement à chaque modèle | ✅ |
| MOB-IOS-UI-02 | Affichage sur iPad | 1. Tester l'application sur iPad | Interface adaptée correctement | ✅ |
| MOB-IOS-UI-03 | Orientation portrait/paysage | 1. Changer l'orientation de l'appareil | Interface adaptée correctement à l'orientation | ✅ |

#### Test des Fonctionnalités Spécifiques à iOS

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| MOB-IOS-FUNC-01 | Notifications | 1. Déclencher une notification<br>2. Vérifier la réception | Notification reçue correctement | ✅ |
| MOB-IOS-FUNC-02 | Stockage local | 1. Enregistrer des données localement<br>2. Fermer l'application<br>3. Rouvrir l'application | Données persistantes correctement | ✅ |
| MOB-IOS-FUNC-03 | Permissions | 1. Demander une permission (ex: notifications)<br>2. Accepter/refuser la permission | Application gère correctement la réponse | ✅ |

## Tests d'Intégration

### Test de l'Intégration Backend-Frontend

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| INT-BF-01 | Authentification de bout en bout | 1. S'inscrire via l'interface frontend<br>2. Se connecter avec les identifiants créés | Inscription et connexion réussies | ✅ |
| INT-BF-02 | Création et affichage d'un article | 1. Créer un article via le backoffice<br>2. Vérifier l'affichage sur le frontend | Article créé et affiché correctement | ✅ |
| INT-BF-03 | Réalisation et enregistrement d'un exercice | 1. Réaliser un exercice de respiration via le frontend<br>2. Vérifier l'enregistrement dans l'historique | Exercice enregistré correctement | ✅ |

### Test de l'Intégration Backend-Mobile

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| INT-BM-01 | Authentification de bout en bout | 1. S'inscrire via l'application mobile<br>2. Se connecter avec les identifiants créés | Inscription et connexion réussies | ✅ |
| INT-BM-02 | Synchronisation de l'historique | 1. Réaliser un exercice sur mobile sans connexion<br>2. Se connecter à Internet<br>3. Vérifier la synchronisation | Historique synchronisé correctement | ✅ |
| INT-BM-03 | Réception de notifications | 1. Configurer une notification sur le backend<br>2. Vérifier la réception sur mobile | Notification reçue correctement | ✅ |

## Tests de Performance

### Test de Charge du Backend

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| PERF-BE-01 | Charge moyenne (50 utilisateurs simultanés) | 1. Simuler 50 utilisateurs simultanés pendant 5 minutes | Temps de réponse < 500ms, pas d'erreurs | ✅ |
| PERF-BE-02 | Charge élevée (200 utilisateurs simultanés) | 1. Simuler 200 utilisateurs simultanés pendant 5 minutes | Temps de réponse < 1s, taux d'erreur < 1% | ✅ |
| PERF-BE-03 | Pic de charge (500 utilisateurs simultanés) | 1. Simuler un pic de 500 utilisateurs pendant 1 minute | Système reste stable, taux d'erreur < 5% | ✅ |

### Test de Performance du Frontend

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| PERF-FE-01 | Temps de chargement initial | 1. Mesurer le temps de chargement initial de l'application | Temps < 2s sur connexion 4G | ✅ |
| PERF-FE-02 | Temps de rendu des listes | 1. Mesurer le temps de rendu d'une liste de 100 articles | Temps < 1s | ✅ |
| PERF-FE-03 | Utilisation mémoire | 1. Surveiller l'utilisation mémoire pendant 30 minutes d'utilisation | Pas de fuites mémoire détectées | ✅ |

### Test de Performance Mobile

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| PERF-MOB-01 | Consommation batterie | 1. Mesurer la consommation batterie pendant 1 heure d'utilisation | Consommation < 15% sur un appareil standard | ✅ |
| PERF-MOB-02 | Utilisation données | 1. Mesurer l'utilisation de données pendant 30 minutes d'utilisation | Utilisation < 10MB | ✅ |
| PERF-MOB-03 | Temps de démarrage | 1. Mesurer le temps entre le lancement de l'app et l'affichage de l'écran d'accueil | Temps < 3s sur un appareil standard | ✅ |

## Tests de Sécurité Globaux

### Test de Vulnérabilités

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| SEC-VUL-01 | Injection SQL | 1. Tester des entrées avec des caractères SQL spéciaux | Pas d'injection possible | ✅ |
| SEC-VUL-02 | XSS (Cross-Site Scripting) | 1. Tester des entrées avec du code JavaScript | Pas d'exécution de code possible | ✅ |
| SEC-VUL-03 | CSRF (Cross-Site Request Forgery) | 1. Tenter des requêtes CSRF | Requêtes bloquées par les protections CSRF | ✅ |

### Test de Gestion des Données Sensibles

| ID | Description | Étapes | Résultat attendu | Statut |
|----|-------------|--------|------------------|--------|
| SEC-DATA-01 | Stockage des mots de passe | 1. Vérifier le stockage des mots de passe en base de données | Mots de passe hashés avec sel | ✅ |
| SEC-DATA-02 | Transmission des données sensibles | 1. Intercepter les communications entre client et serveur | Données sensibles transmises en HTTPS | ✅ |
| SEC-DATA-03 | Stockage local des données sensibles | 1. Examiner le stockage local sur les appareils | Données sensibles chiffrées | ✅ |