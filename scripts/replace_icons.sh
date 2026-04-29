#!/usr/bin/env bash
# replace_icons.sh — Replace mipmap launcher icons with UserLAnd-Next logo.
#
# Usage:
#   ./scripts/replace_icons.sh <path-to-source-logo.png>
#
# The source logo should be at least 192×192 px (xxxhdpi).
# Requires: ImageMagick (convert)
#
# Sizes per density:
#   mdpi    48×48
#   hdpi    72×72
#   xhdpi   96×96
#   xxhdpi  144×144
#   xxxhdpi 192×192

set -euo pipefail

SRC="${1:?Usage: $0 <source-logo.png>}"
RES="app/src/main/res"

declare -A SIZES=(
  [mdpi]=48
  [hdpi]=72
  [xhdpi]=96
  [xxhdpi]=144
  [xxxhdpi]=192
)

for density in "${!SIZES[@]}"; do
  size="${SIZES[$density]}"
  dir="${RES}/mipmap-${density}"
  for icon in ic_launcher.png ic_launcher_round.png ic_launcher_foreground.png; do
    target="${dir}/${icon}"
    if [[ -f "$target" ]]; then
      convert "$SRC" -resize "${size}x${size}" "$target"
      echo "Updated ${target}"
    fi
  done
done

echo "Done. Rebuild the app to see the new icons."
