#!/bin/bash
awslocal s3api head-bucket --bucket attachments 2>/dev/null || awslocal s3 mb s3://attachments
echo "Bucket criado com sucesso!"
