import numpy as np

# The orthognal basis for computing if a 5x5 board is solveable
n1 = np.array([0,1,1,1,0,1,0,1,0,1,1,1,0,1,1,1,0,1,0,1,0,1,1,1,0])
n2 = np.array([1,0,1,0,1,1,0,1,0,1,0,0,0,0,0,1,0,1,0,1,1,0,1,0,1])

class Game:
	"""Class containing the game logic
	"""
	
	def __init__(self, size):
		"""Create a game object
		"""
		self.size = size
		self.lowest_score = float('inf')
		self.wins = 0

		self.freeplay = False
		self.moves = 0
		self.board = self.generate_board()

	def reset(self):
		"""Reset the games move counter and board
		"""
		self.moves = 0
		self.freeplay = False	
		self.board = self.generate_board()
	

	def generate_board(self):
		"""Generate a board of self.size
		Board is always solvable.
		"""
		if self.size == 5:
			board = np.random.rand(5,5) > 0.5
			while not self.is_solvable(board):
				board = np.random.rand(5,5) > 0.5
		else:
			board = np.random.rand(6,6) > 0.5

		return board

	def is_solvable(self, potential_board):
		"""Return true if the board is solvable. Always true when size == 6.
		"""
		if self.size == 6:
			return True
		b_vector = potential_board.reshape((25,)) + 0
		b_dot_n1 = np.dot(b_vector, n1) % 2
		b_dot_n2 = np.dot(b_vector, n2) % 2
		# true if  board forms an orthogonal basis for null(E), 
		# 	where E is the space of solveable boards
		return b_dot_n1 == 0 and b_dot_n2 == 0


	def check_win(self):
		"""Checks if the board is in a win state and returns a bool.
		If it is, increase wins and update low-score. 
		"""
		if (not self.freeplay) and (~self.board).all():
			self.wins += 1
			self.lowest_score = min(self.moves, self.lowest_score)
			return True
		return False

	def toggle(self, row, col):
		"""Toggle the plus-sized area centered at row,col
		"""
		self.moves += 1

		self._toggle(row, col)
		if row - 1 >= 0:
			self._toggle(row - 1, col)
		if col - 1 >= 0:
			self._toggle(row, col - 1)
		if row + 1 < self.size:
			self._toggle(row + 1, col)
		if col + 1 < self.size:
			self._toggle(row, col + 1)


	def _toggle(self, row, col):
		"""Toggle just the single tile at row,col. Used by toggle
		""" 
		self.board[row][col] = not self.board[row,col]
