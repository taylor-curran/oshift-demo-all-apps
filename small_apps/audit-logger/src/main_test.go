package main

import (
	"testing"
)

func TestBasicSetup(t *testing.T) {
	// Basic test to verify testing framework works
	result := 1 + 1
	expected := 2
	
	if result != expected {
		t.Errorf("Expected %d, got %d", expected, result)
	}
}