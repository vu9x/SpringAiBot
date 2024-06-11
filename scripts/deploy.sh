#!/bin/bash

# Поменяйте директорию vn на ваш корневой
ENV_FILE="/home/vn/SpringAiBot/.env"

# Обновление кода и деплой backend приложения
pushd ~/SpringAiBot/ || exit

# Получаем последние изменения
git pull

# Переходим на ветку main
git switch main

# Останавливаем старые контейнеры микросервисов и запускаем новые, с обновлённым кодом
docker compose -f docker-compose.yml --env-file $ENV_FILE down --timeout=60 --remove-orphans
docker compose -f docker-compose.yml --env-file $ENV_FILE up --build --detach

popd || exit