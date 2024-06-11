**Добро пожаловать в проект. Ниже описана инструкция для запуска проекта на локальном компьютере**

**Необходимые программы:**
* Docker - для запуска всех контейнеров на локальном компьютере:
  * Linux: $sudo apt-get install docker-ce=5:26.1.4-1~ubuntu.22.04~jammy
  * Windows: https://www.docker.com/products/docker-desktop/
* ngrok - для имитации https подключения webhook Telegram API:
  * Linux + Windows https://ngrok.com/

**Шаги к запуску:**
1. Установка Docker, ngrok
2. Клонировать репозиторий: https://github.com/vu9x/SpringAiBot.git
   В репозитории находятся 3 ветки:
    * main - главная ветка для деплоя на VPS сервер
    * dev - для разработки на локальном компьютере. Вам необходимо открыть именно этот репозиторий. Здесь настроен docker-compose.yaml для локального развертыванния приложения
    * test - здесь проводил тесты методов внутри модулей. Прошу перейти на эту ветку, если необходимо проверить тесты
3.  Вы находитесь в ветке dev. Вам необходимо добавить в корневую папку SpringAiBot файл .env
4.  Настройка ngrok:
    * Скачать ngrok для Windows: https://ngrok.com/download
    * Установка ngrok в Ubuntu: $ sudo snap install ngrok
    * Для получения Токена необходимо зарегистрироваться на сайте ngrok. И в личном кабинете получить токен.
![image](https://github.com/vu9x/SpringAiBot/assets/46582095/e81c75a2-e414-4713-9af5-0598e10b362a)

    * Добавить токен: $ ngrok config add-authtoken [token]

Запустить утилиту для получения статического адреса на локальный порт 8084:
$ ngrok http 8084
