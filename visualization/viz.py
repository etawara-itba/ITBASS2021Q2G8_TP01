import sys
import numpy as np
import matplotlib
import matplotlib.pyplot as plt
import re


def main(argv):
    if len(argv) < 6:
        raise Exception(
            "Cantidad erronea de argumentos, respetar el siguiente órden:" + "\n" +
            "1- id de partícula" + "\n" +
            "2- archivo de input estático" + "\n" +
            "3- archivo de input dinámico" + "\n" +
            "4- archivo de vecinos" + "\n" +
            "5- radio de interacción" + "\n" +
            "6- tamaño de grilla" + "\n" +
            "7- tiempo a utilizar del archivo estático, opcional (default: 0)"
        )

    # Args to local variables
    particle = int(argv[0])
    input_static_path = argv[1]
    input_dynamic_path = argv[2]
    neighbours_path = argv[3]
    interaction_radius = float(argv[4])
    cell_size = float(argv[5])
    time = argv[6] if (len(argv) > 6) else 0

    # Fetch plot data
    particles, length, radius = get_particles_static(input_static_path)
    particle_time, pos_x, pos_y = get_particles_dynamic(input_dynamic_path, time, particles)
    method_key, simulation_time, neighbours = get_neighbours(neighbours_path)
    category = get_category(particle, particles, neighbours)

    # Plot
    plot(particles, particle_time, interaction_radius, cell_size, length, radius, pos_x, pos_y, category)


def plot(particles, time, interaction_radius, cell_size, length, radius, pos_x, pos_y, category):
    c_particle = []
    c_neighbours = []
    c_others = []

    fig, ax = plt.subplots()

    for i in range(particles):

        circle = plt.Circle((pos_x[i], pos_y[i]), radius=radius[i], linewidth=0, color=get_color(category[i]))

        if category[i] == 0:
            interaction_radius = plt.Circle((pos_x[i], pos_y[i]), radius=interaction_radius, linewidth=1,
                                            fill=False, facecolor=None, edgecolor=get_color(category[i]))
            c_particle.append(interaction_radius)
            c_particle.append(circle)
        elif category[i] == 1:
            c_neighbours.append(circle)
        else:
            c_others.append(circle)
        ax.text(x=pos_x[i], y=pos_y[i], s=str(i))

    col_others = matplotlib.collections.PatchCollection(c_others, match_original=True)
    ax.add_collection(col_others)
    col_neighbours = matplotlib.collections.PatchCollection(c_neighbours, match_original=True)
    ax.add_collection(col_neighbours)
    col_particle = matplotlib.collections.PatchCollection(c_particle, match_original=True)
    ax.add_collection(col_particle)

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


def get_category(particle, particles, neighbours):
    category = []
    particle_neighbours = neighbours[particle]

    for i in range(particles):
        if i == particle:
            category.append(0)
        elif i in particle_neighbours:
            category.append(1)
        else:
            category.append(2)

    return category


def get_color(category):
    color_particle = "b"
    color_neighbour = "g"
    color_other = "r"

    colors = {
        0: color_particle,
        1: color_neighbour,
        2: color_other
    }

    return colors[category]


if __name__ == '__main__':
    main(sys.argv[1:])
