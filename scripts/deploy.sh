#!/bin/bash

ENV_FILE="/home/vn/SpringAiBot/.env"

# Обновление кода и деплой backend приложения
pushd ~/SpringAiBot/ || exit

# Обновляем ветку main
git pull

# Останавливаем старые контейнеры микросервисов и запускаем новые, с обновлённым кодом
docker compose -f docker-compose.yml --env-file $ENV_FILE down --timeout=60 --remove-orphans
docker compose -f docker-compose.yml --env-file $ENV_FILE up --build --detach

popd || exit