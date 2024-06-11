set ENV_FILE=.env

pushd C:\Users\nguye\Documents\tech_spec_diplopma\springAiBot

@REM получаем последние обновления
call git pull

@REM переходим на ветку main
call git switch main

call docker compose -f docker-compose.yml --env-file %ENV_FILE% down --timeout=60 --remove-orphans
call docker compose -f docker-compose.yml --env-file %ENV_FILE% up --build --detach