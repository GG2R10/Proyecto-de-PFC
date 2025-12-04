import re
import csv

# Regex para Aeropuerto("COD", x, y, gmt)
REGEX_AEROP = re.compile(
    r'Aeropuerto\(\s*"([^"]+)"\s*,\s*([-\d]+)\s*,\s*([-\d]+)\s*,\s*([-\d]+)\s*\)'
)

# Regex para Vuelo("ALN", num, "ORG", hs, ms, "DST", hl, ml, esc)
REGEX_VUELO = re.compile(
    r'Vuelo\(\s*"([^"]+)"\s*,\s*([-\d]+)\s*,\s*"([^"]+)"\s*,\s*([-\d]+)\s*,\s*([-\d]+)\s*,\s*"([^"]+)"\s*,\s*([-\d]+)\s*,\s*([-\d]+)\s*,\s*([-\d]+)\s*\)'
)

def parse_scala_file(path_scala, out_aeropuertos, out_vuelos):
    aeropuertos = []
    vuelos = []

    with open(path_scala, "r", encoding="utf-8") as f:
        content = f.read()

        # Buscar TODOS los aeropuertos
        for m in REGEX_AEROP.finditer(content):
            cod, x, y, gmt = m.groups()
            aeropuertos.append([cod, x, y, gmt])

        # Buscar TODOS los vuelos
        for m in REGEX_VUELO.finditer(content):
            aln, num, org, hs, ms, dst, hl, ml, esc = m.groups()
            vuelos.append([aln, num, org, hs, ms, dst, hl, ml, esc])

    # Guardar CSV de aeropuertos
    with open(out_aeropuertos, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["cod", "x", "y", "gmt"])
        writer.writerows(aeropuertos)

    # Guardar CSV de vuelos
    with open(out_vuelos, "w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow(["aln", "num", "org", "hs", "ms", "dst", "hl", "ml", "esc"])
        writer.writerows(vuelos)

    print(f"✔ Aeropuertos encontrados: {len(aeropuertos)}")
    print(f"✔ Vuelos encontrados: {len(vuelos)}")
    print(f"CSV generado:\n  - {out_aeropuertos}\n  - {out_vuelos}")


# Ejemplo de uso:
# parse_scala_file("datos.scala", "aeropuertos.csv", "vuelos.csv")

if __name__ == "__main__":
    import sys
    if len(sys.argv) != 4:
        print("Uso: python parse_scala_to_csv.py archivo.scala aeropuertos.csv vuelos.csv")
        sys.exit(1)
    parse_scala_file(sys.argv[1], sys.argv[2], sys.argv[3])
