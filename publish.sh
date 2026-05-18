#!/bin/bash
# HookSniff Java SDK — Maven Central Publish Script
# Kullanım: ./publish.sh

set -e

SONATYPE_USER="${SONATYPE_USERNAME:-f0wXBf}"
SONATYPE_PASS="${SONATYPE_PASSWORD:-EYLV763IsQVseaffdOXNScf2HZlcLDGEK}"

echo "🔧 Building..."
./gradlew clean compileJava test

echo "📦 Publishing to Maven Local..."
./gradlew publishToMavenLocal

echo "📤 Uploading to Central Portal..."
BUNDLE_DIR="/tmp/central-bundle"
rm -rf "$BUNDLE_DIR"
M2_PATH="$HOME/.m2/repository/io/github/servetarslan02/hooksniff-sdk/1.1.3"
mkdir -p "$BUNDLE_DIR/io/github/servetarslan02/hooksniff-sdk/1.1.3"

cp "$M2_PATH"/*.jar "$M2_PATH"/*.jar.asc "$M2_PATH"/*.pom "$M2_PATH"/*.pom.asc \
   "$M2_PATH"/*.module "$M2_PATH"/*.module.asc \
   "$BUNDLE_DIR/io/github/servetarslan02/hooksniff-sdk/1.1.3/" 2>/dev/null || true

cd "$BUNDLE_DIR"
python3 -c "
import zipfile, os
with zipfile.ZipFile('/tmp/hooksniff-sdk-bundle.zip', 'w', zipfile.ZIP_DEFLATED) as zf:
    for root, dirs, files in os.walk('.'):
        for f in files:
            fp = os.path.join(root, f)
            zf.write(fp, fp[2:])
"

DEPLOY_ID=$(curl -s -X POST "https://central.sonatype.com/api/v1/publisher/upload" \
  -u "$SONATYPE_USER:$SONATYPE_PASS" \
  -F "bundle=@/tmp/hooksniff-sdk-bundle.zip" \
  -F "publishingType=AUTOMATIC")

echo "✅ Uploaded! Deployment ID: $DEPLOY_ID"
echo "🔗 Durum: https://central.sonatype.com/publishing/deployments"
echo "⏳ 15-30 dakika içinde Maven Central'da olacak"
