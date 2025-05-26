#!/bin/bash

# Stop on error
set -e

# Define output dir
OUTPUT_DIR=composeApp/build/dist/wasmJs/productionExecutable

# Create a temp worktree for the gh-pages branch
rm -rf /tmp/gh-pages

# Ensure the gh-pages branch exists
if ! git show-ref --verify --quiet refs/heads/gh-pages; then
  git checkout --orphan gh-pages
  git reset --hard
  git commit --allow-empty -m "Initial commit on gh-pages"
  git switch main
fi

# Add worktree for gh-pages branch
git worktree add /tmp/gh-pages gh-pages

# Clear the gh-pages branch and copy in the site
rm -rf /tmp/gh-pages/*
cp -r $OUTPUT_DIR/* /tmp/gh-pages/

# Commit and push
cd /tmp/gh-pages
touch .nojekyll  # Prevent GitHub Pages from ignoring folders like _module
git add .
git commit -m "Deploy to GitHub Pages" || echo "No changes to commit"
git push origin gh-pages

# Cleanup
cd -
git worktree remove /tmp/gh-pages