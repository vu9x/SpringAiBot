set ENV_FILE=.env

pushd C:\Users\nguye\Documents\tech_spec_diplopma\springAiBot

call git pull

call docker compose -f docker-compose.yml --env-file %ENV_FILE% down --timeout=60 --remove-orphans
call docker compose -f docker-compose.yml --env-file %ENV_FILE% up --build --detach