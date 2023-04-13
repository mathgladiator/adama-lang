#!/bin/sh
mdbook build
surge --domain https://book.adama-platform.com book
