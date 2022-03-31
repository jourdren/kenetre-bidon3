#!/usr/bin/env python3

import sys

lines = []
filename = None
in_comment = True

for line in sys.stdin:
    if line.startswith('#'):
        if not in_comment:
            with open(filename, 'a') as out:
                for l in lines:
                    out.write(l)
            lines.clear()
            in_comment = True

        if ',' in line:
            filename = line.split(',')[0][2:] + 'MetricsOut.csv'
    
    else:
        in_comment = False

    lines.append(line)

with open(filename, 'a') as out:
    for l in lines:
        out.write(l)
