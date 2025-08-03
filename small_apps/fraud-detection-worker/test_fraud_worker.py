import pytest

def test_basic_setup():
    """Basic test to verify pytest is working"""
    assert 1 + 1 == 2

def test_imports():
    """Test that required modules can be imported"""
    try:
        import pika
        import redis
        import sklearn
        import numpy
        import pandas
        assert True
    except ImportError as e:
        pytest.fail(f"Failed to import required module: {e}")