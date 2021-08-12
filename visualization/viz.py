import sys
import numpy as np
import matplotlib.pyplot as plt
import re


def main(argv):
    if len(argv) < 5:
        raise Exception("Not enough arguments")

    # Args to local variables
    particle = int(argv[0])
    input_static_path = argv[1]
    input_dynamic_path = argv[2]
    neighbours_path = argv[3]
    cell_size = argv[4]
    time = argv[5] if (len(argv) > 5) else 0

    # Fetch plot data
    particles, length, radius = get_particles_static(input_static_path)
    particle_time, pos_x, pos_y = get_particles_dynamic(input_dynamic_path, time, particles)
    neighbours = get_neighbours(neighbours_path)
    colors = get_colors(particle, particles, neighbours)

    # Plot
    plot(particles, particle_time, cell_size, length, radius, pos_x, pos_y, colors)


def plot(particles, time, cell_size, length, radius, pos_x, pos_y, colors):
    fig, ax = plt.subplots()
    ax.scatter(x=pos_x, y=pos_y, c=colors, s=radius)

    for i in range(particles):
        ax.text(x=pos_x, y=pos_y, s=str(i))

    ax.set_xlim([0, length])
    ax.set_ylim([0, length])
    ax.set_xticks(np.arange(0, length, cell_size))
    ax.set_yticks(np.arange(0, length, cell_size))

    ax.set_xlabel('position X', fontsize=15)
    ax.set_ylabel('position Y', fontsize=15)
    ax.set_title('Position of particle and neighbours at time={:d}'.format(time))

    ax.grid(True)
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
                if not evaluate and current_time > time:
                    evaluate = True
                    particles_time = current_time
                elif evaluate and current_time > time:
                    break
            elif evaluate:
                stripped_line = list(map(lambda n: float(n), re.split(r'\t+', line.strip())))
                pos_x.append(stripped_line[0])
                pos_y.append(stripped_line[1])
            current_line += 1

    return particles_time, np.array(pos_x), np.array(pos_y)


def get_neighbours(neighbours_path):
    neighbours = {}

    with open(neighbours_path, "r") as f:
        for line in f:
            stripped_line = line.replace("[", "").replace("]", "").strip()
            line_array = np.fromiter(map(lambda n: int(n), re.split(r'\t+', stripped_line)), dtype=int)

            neighbours[line_array[0]] = line_array[1:]

    return neighbours


def get_colors(particle, particles, neighbours, color_particle="blue", color_neighbour="green", color_other="red"):
    colors = []
    particle_neighbours = neighbours[particle]

    for i in range(particles):
        if i == particle:
            colors.append(color_particle)
        elif particle_neighbours.contains(i):
            colors.append(color_neighbour)
        else:
            colors.append(color_other)

    return colors


if __name__ == '__main__':
    main(sys.argv[1:])
