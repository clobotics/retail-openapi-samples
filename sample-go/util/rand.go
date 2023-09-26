package util

import (
	"math/rand"
	"time"
)

func GenRandomString(length int) string {
	rand.Seed(time.Now().UnixNano())

	charset := "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	randomString := make([]byte, length)

	for i := 0; i < length; i++ {
		randomString[i] = charset[rand.Intn(len(charset))]
	}

	return string(randomString)
}
