**Добро пожаловать в проект.**

Ссылка на разработанный телеграм бот: https://t.me/R2D2_gpt_bot 

Чатбот: R2D2.

Умеет отвечать на ваши текстовые сообщения с помощью ChatGPT, а также сохранять ваши фото и документы на сервере для дальнейшего скачивания.



**Ниже описана инструкция для запуска проекта на локальном компьютере:**

**Необходимые программы:**
* Docker - для запуска всех контейнеров на локальном компьютере:
  * Linux: $sudo apt-get install docker-ce=5:26.1.4-1~ubuntu.22.04 ~jammy (Прошу убрать пробел между "22.04 ~jammy")
  * Windows: https://www.docker.com/products/docker-desktop/
* ngrok - для имитации https подключения, который необходим webhook Telegram API:
  * Linux + Windows https://ngrok.com/

**Шаги к запуску на локальном компьютере**
1. Установка Docker, ngrok
2. Клонировать репозиторий: https://github.com/vu9x/SpringAiBot.git
   В репозитории находятся 3 ветки:
    * main - главная ветка для деплоя на VPS сервер
    * **dev** - для разработки на локальном компьютере. Вам необходимо открыть именно этот репозиторий. Здесь настроен docker-compose.yaml для локального развертыванния приложения
    * **test** - здесь проводил тесты методов внутри модулей. Прошу перейти на эту ветку, если необходимо проверить тесты
3.  Вы находитесь в ветке dev. Вам необходимо добавить в корневую папку SpringAiBot файл .env
4.  Настройка ngrok:
    * Скачать ngrok для Windows: https://ngrok.com/download
    * Установка ngrok в Ubuntu: $ sudo snap install ngrok
    * Для получения Токена необходимо зарегистрироваться на сайте ngrok. И в личном кабинете получить токен.
![image](https://github.com/vu9x/SpringAiBot/assets/46582095/e81c75a2-e414-4713-9af5-0598e10b362a)
    * Для Windows небходимо запустить программу ngrok.exe
    * Добавить токен: $ ngrok config add-authtoken [token]
    * Запустить утилиту для получения статического адреса на локальный порт 8084: $ ngrok http 8084
![image](https://github.com/vu9x/SpringAiBot/assets/46582095/0529f1ee-0614-4786-b377-9fdd89005755)
На скриншоте - это https://95a0-113-161-83-135.ngrok-free.app 
    * Скопировать сгенерированный сайт с https в файл .env в параметр TG_BOT_URI
5. Убериться что докер запущен

6. Подготовка к запуску. В корневом проекте в папке scripts подготовлены 2 исполняемых файла: deploy.sh, deploy.bat.
    * Запуск с Windows:
     * Внутри файла deploy.bat заменить путь переменной "pushd" на абсолютный путь к папке SpringAiBot
     * Открыть терминал и перейти в папку SpringAiBot. Запустить команду scripts\deploy.bat

    * Запуск с Linux-ОС:
     * Заменить в параметре ENV_FILE, /home/**vn**, на вашего пользователя в системе
     * Открыть терминал и запустить команду /bin/bash ~/SpringAiBot/scripts/deploy.sh

После успешного развертывания приложения можно тестировать чатбот R2D2: https://t.me/R2D2_gpt_bot 

**Деплой на Virtual Private Server:**
* Был арендован польский сервер компании https://timeweb.cloud/, так как OpenAi API не подерживает запросы через серверы, которые находятся в России.
* Был выкуплен домен: https://telegramaibot.ru/
* Выпущен и подключен бесплатный сертификат: https://letsencrypt.org/
* Подключен traefik для балансировки и шифрования данных взаимодействия внутри компонентов программ. Можно подключиться к дашборду траефика через данные ниже
  * https://telegramaibot.ru:9443/dashboard/#/
  * login: admin
  * password: password
* Настроил ssh подключение между VPS и Github, для получения последних версии кода
* Так как нельзя выкладывать чувствительные данные, такие как пароли и ключи API, на GitHub. То все файл .env со всеми паролями и API ключами был передан с локального компьютера на VPS через по SFTP к серверу через FileZilla.
