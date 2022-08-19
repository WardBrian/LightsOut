from setuptools import setup, find_packages

setup(
	name="py_lights_out",
	version="0.0.2",
    author="Brian Ward",
    author_email="brianmward99@gmail.com",
    description="A command line version of Lights Out!",
	packages=find_packages(),
	entry_points={
        'console_scripts': [
            'pylo = py_lights_out:main',
        ],
    },
    )
