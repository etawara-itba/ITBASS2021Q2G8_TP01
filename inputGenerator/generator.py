import sys
import random

# primero valido que me pasen la cantidad exacta de parámetros
if len(sys.argv) < 3 or len(sys.argv) > 4:
    print('ERROR: Cantidad incorrecta de argumentos\n En primer lugar, debes especificar el número de partículas, luego el área de la simulación y, por último, el radio de las partículas. Este último argumneto es opcional.')
    sys.exit(1)

# tomo los valores de los argumentos (el primer argumento corresponde al nombre del programa)
N = int(sys.argv[1])
L = int(sys.argv[2])

# chequeo que los números sean correctos
if N <= 0:
    print('ERROR: El número de partículas debe ser un número entero mayor a 0')
    sys.exit(1)

if L <= 0:
    print('ERROR: El valor del área de simulación debe ser un número entero mayor a 0')
    sys.exit(1)


if len(sys.argv) == 4:
    rad = float(sys.argv[3])
    if rad < 0:
        print('ERROR: El valor del radio de las partículas debe ser un número entero mayor a 0')
        sys.exit(1)
else:
    rad = None


class Particule(object):
    def __init__(self, id, coordX, coordY, radius):
        self.id = id
        self.coordX = coordX
        self.coordY = coordY
        self.radius = radius


particles = []
for i in range(N):
    if rad:
        radius = rad
    else:
        radius = random.uniform(0, 1)
    particles.append(Particule(i + 1, random.uniform(0, L), random.uniform(0, L), radius))


static_file = open('StaticFile.txt', "w")
static_file.write(str(N))
static_file.write('\n')
static_file.write(str(L))
for particle in particles:
    static_file.write('\n')
    static_file.write('%.4F' % particle.radius)
    static_file.write(' ')
    static_file.write('color')
static_file.close()

dynamic_file = open('DynamicFile.txt', "w")
dynamic_file.write('0')
for particle in particles:
    dynamic_file.write('\n')
    dynamic_file.write('{}'.format(particle.coordX))
    dynamic_file.write(' ')
    dynamic_file.write('{}'.format(particle.coordY))
    dynamic_file.write(' ')
    # la velocidad inicial es cero tanto en el eje x como en el y
    dynamic_file.write(str(0))
    dynamic_file.write(' ')
    dynamic_file.write(str(0))
dynamic_file.close()

print('Archivos generados')

