# SpriteSheetCreator
SpriteSheetCreator - генератор текстурных атласов.
Принимает на входе каталог со спрайтами в формате png и генерит json файл с координатной сеткой и текстуры.
Максимальный размер текстуры 1024x1024 пикселей.

## Сборка
javac SpriteSheetCreator.java

## Использование
java SpriteSheetCreator input_dir output_dir

input_dir - путь к каталогу со спрайтами, которые надо упаковать в текстурный атлас.

output_dir - путь к каталогу с результатом работы.
