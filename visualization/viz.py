import sys
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
import re


def main(argv):
    if len(argv) < 5:
        raise Exception(
            "Cantidad erronea de argumentos, respetar el siguiente órden:" + "\n" +
            "1- id de partícula" + "\n" +
            "2- archivo de input estático" + "\n" +
            "3- archivo de input dinámico" + "\n" +
            "4- archivo de vecinos" + "\n" +
            "5- tamaño de grilla" + "\n" +
            "6- tiempo a utilizar del archivo estático, opcional (default: 0)"
        )

    # Args to local variables
    particle = int(argv[0])
    input_static_path = argv[1]
    input_dynamic_path = argv[2]
    neighbours_path = argv[3]
    cell_size = float(argv[4])
    time = argv[5] if (len(argv) > 5) else 0

    # Fetch plot data
    particles, length, radius = get_particles_static(input_static_path)
    particle_time, pos_x, pos_y = get_particles_dynamic(input_dynamic_path, time, particles)
    method_key, simulation_time, neighbours = get_neighbours(neighbours_path)
    colors, zorder = get_colors(particle, particles, neighbours)

    # Plot
    plot(particles, particle_time, cell_size, length, radius, pos_x, pos_y, colors, zorder)


def plot(particles, time, cell_size, length, radius, pos_x, pos_y, colors, zorder):
    circles = []

    fig, ax = plt.subplots()

    for i in range(particles):
        circles.append(
            plt.Circle((pos_x[i], pos_y[i]), radius=radius[i], linewidth=0, color=colors[i], zorder=zorder[i]))
        ax.text(x=pos_x[i], y=pos_y[i], s=str(i))

    c = matplotlib.collections.PatchCollection(circles, match_original=True)
    ax.add_collection(c)

    ax.set_xlim([0, length])
    ax.set_ylim([0, length])
    ax.set_xticks(np.arange(0, length, cell_size))
    ax.set_yticks(np.arange(0, length, cell_size))

    ax.set_xlabel('position X', fontsize=15)
    ax.set_ylabel('position Y', fontsize=15)
    ax.set_title('Position of particle and neighbours at time={:d}'.format(time))

    ax.grid(True)
    ax.set_aspect('equal')
    plt.show()


def get_particles_static(static_file_path):
    current_line = 0
    lines = 0
    length = 0
    radius = []
    with open(static_file_path, "r") as f:
        for line in f:
            if current_line == 0:
                lines = int(line.strip())
            elif current_line == 1:
                length = int(line.strip())
            else:
                radius.append(float(re.split(r'\t+', line.strip())[0]))

            current_line += 1

    return lines, length, radius


def get_particles_dynamic(dynamic_file_path, time, particles):
    pos_x = []
    pos_y = []
    particles_time = 0
    current_line = 0
    current_time = 0
    evaluate = False

    with open(dynamic_file_path, "r") as f:
        for line in f:
            # +1 because the line with the time creates an offset
            if current_line % (particles + 1) == 0:
                current_time = int(line.strip())
                if not evaluate and current_time >= time:
                    evaluate = True
                    particles_time = current_time
                elif evaluate and current_time >= time:
                    break
            elif evaluate:
                stripped_line = list(map(lambda n: float(n), re.split(r'\t+', line.strip())))
                pos_x.append(stripped_line[0])
                pos_y.append(stripped_line[1])
            current_line += 1

    return particles_time, np.array(pos_x), np.array(pos_y)


def get_neighbours(neighbours_path):
    neighbours = {}
    current_line = 0
    method_key = ""
    time = 0

    with open(neighbours_path, "r") as f:
        for line in f:

            if current_line == 0:
                method_key = re.split(r'\t+', line)[1].strip()
            elif current_line == 1:
                time = re.split(r'\t+', line)[1].strip()
            else:
                stripped_line = line.replace("[", "").replace("]", "").strip()
                line_array = np.fromiter(map(lambda n: int(n), re.split(r'\t+', stripped_line)), dtype=int)

                neighbours[line_array[0]] = line_array[1:]
            current_line += 1

    return method_key, time, neighbours


def get_colors(particle, particles, neighbours, color_particle="b", color_neighbour="g", color_other="r"):
    colors = []
    zorder = []
    particle_neighbours = neighbours[particle]

    for i in range(particles):
        if i == particle:
            colors.append(color_particle)
            zorder.append(10)
        elif i in particle_neighbours:
            colors.append(color_neighbour)
            zorder.append(5)
        else:
            colors.append(color_other)
            zorder.append(0)

    return colors, zorder


if __name__ == '__main__':
    main(sys.argv[1:])
