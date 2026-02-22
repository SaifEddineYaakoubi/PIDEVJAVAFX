# 🔑 SOURCES RÉELLES POUR OBTENIR DES API KEYS GRATUITES

## 📊 API #1: FAO PRICES - ALTERNATIVES RÉELLES

### ✅ OPTION 1: World Bank Climate Data API (GRATUIT)
```
URL: https://api.worldbank.org/v2/country/TUN/indicator/AG.YLD.CREL.KG
Description: Données agricoles réelles du Monde Banque
Couverture: Rendements agricoles par pays
Clé API: PAS REQUISE (gratuit)
Taux: Illimité
```

**Utilisation:**
```java
private static final String API_BASE_URL = 
    "https://api.worldbank.org/v2/country/TUN/indicator";

// Récupérer les données
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(API_BASE_URL + "/AG.YLD.CREL.KG?format=json"))
    .GET()
    .build();
```

---

### ✅ OPTION 2: QUANDL - Données Agricoles (GRATUIT avec inscription)
```
URL: https://www.quandl.com/
Description: Données économiques et agricoles réelles
Inscription: Gratuit (https://www.quandl.com/sign-up)
Clé API: Générée après inscription
Taux: 50 requêtes/jour (gratuit)
```

**Inscription:**
1. Aller sur https://www.quandl.com/sign-up
2. Créer compte gratuit
3. Aller sur Account Settings
4. Copier votre API Key
5. Utiliser:

```java
private static final String API_KEY = "YOUR_QUANDL_API_KEY";
private static final String API_BASE_URL = "https://www.quandl.com/api/v3/datasets";

// Exemple: Données prix blé
String url = "https://www.quandl.com/api/v3/datasets/FRED/WASULTW?api_key=" + API_KEY;
```

---

### ✅ OPTION 3: Alpha Vantage - Données Financières (GRATUIT)
```
URL: https://www.alphavantage.co/
Description: Prix commodités et données financières réelles
Inscription: Gratuit (https://www.alphavantage.co/voice)
Clé API: Générée immédiatement par email
Taux: 5 requêtes/minute, illimitées par jour
```

**Inscription:**
1. Aller sur https://www.alphavantage.co/
2. Remplir formulaire (nom, email)
3. Cliquer "GET FREE API KEY"
4. Vérifier email pour votre API KEY
5. Utiliser:

```java
private static final String API_KEY = "YOUR_ALPHA_VANTAGE_KEY";
private static final String API_BASE_URL = "https://www.alphavantage.co/query";

// Exemple: Prix or/argent
String url = API_BASE_URL + 
    "?function=CURRENCY_EXCHANGE_RATE" +
    "&from_currency=TND" +
    "&to_currency=USD" +
    "&apikey=" + API_KEY;
```

---

### ✅ OPTION 4: FRED API (Banque Fédérale US) - GRATUIT
```
URL: https://fred.stlouisfed.org/
Description: Données économiques réelles du gouvernement US
Inscription: Gratuit (https://fredaccount.stlouisfed.org/user/register)
Clé API: Générée après inscription
Taux: Illimité
```

**Inscription:**
1. Aller sur https://fredaccount.stlouisfed.org/user/register
2. Créer compte gratuit
3. Aller sur API Key Management
4. Copier votre clé
5. Utiliser:

```java
private static final String API_KEY = "YOUR_FRED_API_KEY";
private static final String API_BASE_URL = "https://api.stlouisfed.org/fred";

// Exemple: Indices agricoles
String url = API_BASE_URL + "/series/data" +
    "?series_id=WHEAT&api_key=" + API_KEY;
```

---

## 🤖 API #2: PRÉDICTIONS IA - ALTERNATIVES RÉELLES

### ✅ OPTION 1: IBM Watson ML (GRATUIT avec compte Cloud)
```
URL: https://cloud.ibm.com/
Description: Machine Learning d'IBM
Inscription: Gratuit (créer compte IBM Cloud)
Plan gratuit: 500 heures/mois
```

**Inscription:**
1. Aller sur https://cloud.ibm.com/login
2. Cliquer "Create Account"
3. Remplir formulaire
4. Créer nouvel API Key dans Account Settings
5. Utiliser:

```java
private static final String IBM_ENDPOINT = 
    "https://api.eu-de.ml.cloud.ibm.com/ml/v4/deployments/YOUR_DEPLOYMENT_ID/predictions";
    
private static final String IBM_API_KEY = "YOUR_IBM_API_KEY";
```

---

### ✅ OPTION 2: Hugging Face - AI Models (GRATUIT)
```
URL: https://huggingface.co/
Description: Modèles ML gratuits pré-entraînés
Inscription: Gratuit (https://huggingface.co/join)
Clé API: Générée après inscription
Taux: Illimité (hébergé)
Modèles: 1M+ modèles disponibles
```

**Inscription:**
1. Aller sur https://huggingface.co/join
2. Créer compte gratuit
3. Aller sur Settings → Access Tokens
4. Créer nouveau token "read"
5. Copier le token
6. Utiliser:

```java
private static final String HF_API_KEY = "hf_YOUR_TOKEN_HERE";
private static final String HF_ENDPOINT = 
    "https://api-inference.huggingface.co/models/facebook/bart-large-mnli";

// Utiliser un modèle agricole
String url = "https://api-inference.huggingface.co/models/YOUR_MODEL";

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create(url))
    .header("Authorization", "Bearer " + HF_API_KEY)
    .POST(HttpRequest.BodyPublishers.ofString(payload))
    .build();
```

---

### ✅ OPTION 3: Replicate - AI API (GRATUIT avec crédit)
```
URL: https://replicate.com/
Description: API pour modèles ML (Stable Diffusion, etc.)
Inscription: Gratuit (https://replicate.com/signin)
Clé API: Générée après inscription
Taux: Crédit gratuit $25 à l'inscription
```

**Inscription:**
1. Aller sur https://replicate.com/signin
2. Créer compte (GitHub ou Email)
3. Aller sur API Tokens
4. Copier votre token
5. Utiliser:

```java
private static final String REPLICATE_API_KEY = "YOUR_REPLICATE_TOKEN";
private static final String REPLICATE_ENDPOINT = 
    "https://api.replicate.com/v1/predictions";
```

---

### ✅ OPTION 4: Google Colab + TensorFlow (100% GRATUIT)
```
URL: https://colab.research.google.com/
Description: Jupyter notebooks avec GPU gratuit
Inscription: Compte Google
Taux: Complètement GRATUIT
Alternative: Créer votre propre API local
```

**Utilisation:**
1. Aller sur https://colab.research.google.com/
2. Créer nouveau notebook
3. Entraîner modèle ML avec vos données
4. Exporter modèle (.pkl ou .h5)
5. Utiliser localement dans votre app (pas besoin d'API externe!)

```java
// Charger modèle local entraîné
String modelPath = "models/prediction_model.pkl";
// Utiliser modèle pour prédictions locales
```

---

## 🎯 MA RECOMMANDATION POUR VOUS

### 📊 Pour FAO Prices (Tarifs Agricoles):
**MEILLEURE OPTION:** Alpha Vantage ⭐⭐⭐⭐⭐
```
✅ GRATUIT (illimité par jour)
✅ Inscription rapide (2 minutes)
✅ API Key par email immédiatement
✅ Données réelles et à jour
✅ Bon pour prix commodités
✅ Facile à intégrer
```

**ALTERNATIVE:** Quandl
```
✅ GRATUIT (50 requêtes/jour)
✅ Inscription gratuite
✅ Plus de données agricoles spécifiques
✅ Très complet
```

### 🤖 Pour Prédictions IA:
**MEILLEURE OPTION:** Hugging Face ⭐⭐⭐⭐⭐
```
✅ GRATUIT (100% gratuit, pas de crédit)
✅ 1M+ modèles ML disponibles
✅ Modèles agricoles existants
✅ Très facile à intégrer
✅ Pas de limite d'utilisation
✅ Community active
```

**ALTERNATIVE:** Google Colab (LOCAL)
```
✅ GRATUIT (GPU gratuit)
✅ Entraîner votre propre modèle
✅ Plus de contrôle
✅ Modèle local = plus rapide
✅ Pas de dépendance API externe
```

---

## 🚀 ÉTAPES CONCRÈTES - À FAIRE MAINTENANT

### Étape 1: Obtenir Alpha Vantage API Key (5 min)
```
1. Aller sur https://www.alphavantage.co/
2. Remplir le formulaire (nom, email, sélectionner "Individual")
3. Cliquer "GET FREE API KEY"
4. Vérifier votre email
5. Copier la clé
```

### Étape 2: Obtenir Hugging Face Token (5 min)
```
1. Aller sur https://huggingface.co/join
2. Créer compte (Email ou GitHub)
3. Aller sur https://huggingface.co/settings/tokens
4. Cliquer "New token"
5. Nommer: "SmartFarm" 
6. Accès: "read"
7. Copier le token
```

### Étape 3: Tester les APIs (10 min)
```bash
# Tester Alpha Vantage
curl "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=TND&to_currency=USD&apikey=YOUR_KEY"

# Tester Hugging Face
curl -X POST \
  -H "Authorization: Bearer YOUR_HF_TOKEN" \
  https://api-inference.huggingface.co/models/facebook/bart-large-mnli \
  -d '{"inputs":"This is a test"}'
```

---

## 📝 MISE À JOUR DU CODE

### Mise à jour FAOPricesService.java
```java
// Remplacer:
private static final String API_BASE_URL = 
    "https://fenixservices.fao.org/faostat/api/v1/data";
private static final String API_KEY = "YOUR_FAO_API_KEY";

// Par:
private static final String API_BASE_URL = 
    "https://www.alphavantage.co/query";
private static final String API_KEY = "YOUR_ALPHA_VANTAGE_KEY";  // Obtenu ci-dessus
```

### Mise à jour AIPredictionService.java
```java
// Remplacer:
private static final String GOOGLE_ENDPOINT = 
    "https://ml.googleapis.com/v1/projects/YOUR_PROJECT/predict";
private static final String GOOGLE_API_KEY = "YOUR_GOOGLE_API_KEY";

// Par:
private static final String HF_ENDPOINT = 
    "https://api-inference.huggingface.co/models/YOUR_MODEL";
private static final String HF_API_KEY = "hf_YOUR_TOKEN_HERE";  // Obtenu ci-dessus
```

---

## ✅ CHECKLIST - ACTIONS À FAIRE

- [ ] Créer compte Alpha Vantage
- [ ] Copier API Key Alpha Vantage
- [ ] Créer compte Hugging Face
- [ ] Copier token Hugging Face
- [ ] Tester les URLs avec curl
- [ ] Mettre à jour le code (2 fichiers)
- [ ] Recompiler et tester
- [ ] Commiter sur Git

---

**TEMPS TOTAL:** ~20 minutes pour avoir les vraies clés API et tout configuré!

C'est vraiment GRATUIT, RÉEL et SIMPLE! 🚀

