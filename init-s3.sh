#!/bin/bash

create_s3_bucket() {
  local BUCKET_NAME=$1
  local ENABLE_VERSIONING=$2

  echo "Verificando bucket: $BUCKET_NAME"

  # Verifica se o bucket já existe para evitar erro de 'BucketAlreadyOwnedByYou'
  if ! awslocal s3api head-bucket --bucket "$BUCKET_NAME" 2>/dev/null; then
    echo "Criando bucket: $BUCKET_NAME..."
    awslocal s3 mb "s3://$BUCKET_NAME"

    if [ "$ENABLE_VERSIONING" = "true" ]; then
      echo "Ativando versionamento para: $BUCKET_NAME"
      awslocal s3api put-bucket-versioning --bucket "$BUCKET_NAME" --versioning-configuration Status=Enabled
    fi
  else
    echo "Bucket $BUCKET_NAME já existe. Pulando criação."
  fi
}

create_s3_bucket "attachments" "false"

create_s3_bucket "archive-storage" "true"

echo "S3 Initialized!"
