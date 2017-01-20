# StemmingAndSpellChecking

Сравнение результатов работы стеммера на словах с ошибками и опечатками и исправленных словах.

Используется библиотека [Languagetool](https://languagetool.org/ru/)

Стемминг с помощью Стеммера Портера

### Начало работы
- Склонируйте репозиторий
```sh
$ git clone https://github.com/GuminEgor/StemmingAndSpellChecking.git
```

- Загрузите train.csv с сайта [SNA Hakathon 2014](http://sh2014.org/task) и поместите в директорию resources
- Запустите Main.java
- В результате работы будет сгенерирован файл res.csv, опираясь на который Вы сможете построить диаграмму частотности (пример в файле Diagram.xlsx)
