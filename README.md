### Лабы по компьютерному зрению (2 семестр магистратуры ПИ АлтГТУ)
#### Автор: Фаст Артем

* (Сделано). Лабораторная работа 1. Свертка
    * Написать основу для представления изображений и их обработки свертками
      * Раздельная обработка по осям для сепарабельных фильтров
      * Различные варианты обработки краевых эффектов при свертке
    * Реализовать вычисление частных производных и оператора Собеля
    * Реализовать отображение полученных результатов
    
* (Сделано). Лабораторная работа 2. Scale space
  * Из заданного изображения построить гауссову пирамиду
    * Устанавливается количество октав (можно вычислять исходя из размера изображения)
    * Устанавливается число уровней в октаве 
    * Устанавливается σ<sub>0</sub> и σ<sub>1</sub>
  * Реализовать функцию L(x, y, σ)
    * Поиск ближайшего изображения 
    * Преобразование координат
  * Реализовать отображение результатов 
    * σ на каждом масштабе в октаве 
    * эффективная σ для каждого масштаба 
  * (Пов. сложность) реализовать построение пирамиды с -1 октавы

* (Сделано). Лабораторная работа 3. Операторы точек интереса
  * Реализовать операторы Моравека и Харриса для поиска интересных точек в изображении
  * Реализовать фильтрацию интересных точек методом Adaptive Non-Maximum Suppression для заданного количества необходимых точек
  * Оценить повторяемость результата при некоторых искажениях оригинального изображения: сдвиг, поворот, шум, контрастность и яркость
  * Сравнить выдачу операторов Моравека и Харриса по повторяемости
  * (Пов. сложность) Реализовать алгоритм поиска краев Кэнни
    
* (Сделано). Лабораторная работа 4.
  * Реализовать вычисление дескрипторов окрестностей заданных точек путем вычисления градиентов в каждой точки изображения и разбиения окрестности на сетку
  * Реализовать вычисление гистограмм градиентов в ячейках сетки и нормализацию полученных дескрипторов
  * Реализовать визуализацию результатов поиска ближайших дескрипторов в двух изображениях.
    
* (Сделано). Лабораторная работа 5.
  * Реализовать относительную инвариантность вычисления дескрипторов к вращению изображений на основе подхода SIFT.
  * Реализовать этап оценки ориентации интересной точки и поворота сетки, в которой вычисляются гистограммы градиентов.
  * Оценить полученный алгоритм с точки зрения реакции на соответствующие искажения изображений, сравнить с полученным в четвертой работе.
    
* (Сделано). Лабораторная работа 6.
  * Реализовать подобную методу SIFT схему выбора масштаба изображения, на котором будет рассчитываться дескриптор окрестности интересной точки. Для этого включить построение гауссовой пирамиды с разделением на октавы в фазу подготовки изображения.
  * Реализовать алгоритм поиска экстремумов в наборе DoG. Внести соответствующие изменения в алгоритм поиска интересных точек и вычисления дескриптора окрестности.
  * Сравнить полученные результаты, сравнить с полученными в пятой работе.
    
* (Сделано). Лабораторная работа 7.
  * Реализовать распределение значений градиентов по смежным гистограммам, добавить весовые коэффициенты исходя из расстояния до соответствующих центров.
  * Опционально – локальное уточнение центра дескриптора
  * Оценить работу дескриптора исходя из искажений поворота плоскости, оценить повторяемость в этом случае.
  * Сравнить полученные результаты, сравнить с полученными в шестой работе.
    
* (Не сделано). Лабораторная работа 8.
  * Даны два изображения, одно является сдвигом, смещением, вращением, изменением ракурса, либо все вместе.
  * С помощью извлеченных наборов дескрипторов из обоих изображений и их соответствий, найти модель трансформации между изображениями, представленной в виде матрицы выбранного преобразования.
  * Продемонстрировать полученные результаты на примере сшивания исходных изображений исходя из полученной матрицы.
    
* (Не сделано). Лабораторная работа 9.
