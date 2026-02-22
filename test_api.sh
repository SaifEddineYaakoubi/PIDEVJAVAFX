#!/bin/bash

# Test simple de l'API Alpha Vantage

echo "🧪 TEST API ALPHA VANTAGE"
echo "════════════════════════════════════════"
echo ""

API_KEY="2IQ6VVI25Z0ROT5Y"
API_URL="https://www.alphavantage.co/query"

echo "📡 Appel 1: Test avec devise réelle (USD)"
echo "─────────────────────────────────────────"
curl -s "${API_URL}?function=CURRENCY_EXCHANGE_RATE&from_currency=USD&to_currency=EUR&apikey=${API_KEY}" | jq '.' 2>/dev/null || echo "Réponse reçue (JSON parsing échoué)"

echo ""
echo "📡 Appel 2: Test avec commodity (Tomate)"
echo "──────────────────────────────────────────"
curl -s "${API_URL}?function=CURRENCY_EXCHANGE_RATE&from_currency=Tomate&to_currency=USD&apikey=${API_KEY}" | jq '.' 2>/dev/null || echo "Réponse reçue (JSON parsing échoué)"

echo ""
echo "════════════════════════════════════════"
echo "✅ Test terminé"

