#!/bin/bash
# Build and Run Script for Smart Farm Application

echo "================================"
echo "🔨 Building Smart Farm Application"
echo "================================"

# Clean and compile
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "🧪 Running Database Connection Test..."
    echo ""

    # Build classpath
    CLASSPATH="target/classes"
    for jar in $(find ~/.m2 -name "*.jar" -path "*/mysql*" -o -name "javafx*" 2>/dev/null); do
        CLASSPATH="$CLASSPATH:$jar"
    done

    # Run test
    java -cp "$CLASSPATH" org.example.pidev.test.TestDBConnection

    echo ""
    echo "================================"
    echo "📋 Ready to run the application!"
    echo "================================"
else
    echo "❌ Build failed!"
    exit 1
fi

