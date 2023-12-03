# Сборка и запуск приложения
## Старт приложения
В корне проекта выполняем последовательно:
```sh
mvn clean install
```

```sh
docker build -t weather-service:0.1.0 .
```

Останавливаем ранее запущенный compose-файл
```shell
docker compose down
```

Запускаем compose-файл
```shell
docker compose up -d
```

Открываем сваггер приложения по ссылке: http://localhost:8080/swagger-ui.html