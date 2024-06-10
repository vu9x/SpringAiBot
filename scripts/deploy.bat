set ENV_FILE=.env

pushd C:\Users\nguye\Documents\tech_spec_diplopma\springAiBot

@REM @REM # получаем последние обновления
@REM call git pull
@REM
@REM @REM # переходим на ветку dev
@REM call git switch dev

call docker compose -f docker-compose.yml --env-file %ENV_FILE% down --timeout=60 --remove-orphans
call docker compose -f docker-compose.yml --env-file %ENV_FILE% up --build --detach