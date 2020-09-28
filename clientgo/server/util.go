package main

import "math/rand"

func getRandomBytes(n int) []byte {
	b := make([]byte, n)
	rand.Read(b)
	return b
}
