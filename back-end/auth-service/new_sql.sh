#!/bin/bash

now=$(date +"%Y%m%d_%H%M")

read -p "File name (please using underscore without .sql): " desc

filename="${now}_${desc}.sql"

target_dir="src/main/resources/database"

mkdir -p "$target_dir"

touch "$target_dir/$filename"

echo "✅ File SQL successfully created: $target_dir/$filename"
