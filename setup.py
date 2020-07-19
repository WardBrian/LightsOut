from setuptools import setup, find_packages

setup(
	name="py_lights_out", 
	version="0.0.1",
    author="Brian Ward",
    author_email="wardbp@bc.edu",
    description="A command line version of Lights Out!",
	packages=find_packages(),
	entry_points={
        'console_scripts': [
            'pylo = py_lights_out:main',
        ],
    },
    )