import py_cui, pyfiglet

import game

class LightsOutCUI:

	
	def get_logo(self):
		"""Return the ascii logo of the game
		"""
		out = r""
		out += r"   __   _____  ___         _____  __    " + '\n'
		out += r"  / /   \_   \/ _ \ /\  /\/__   \/ _\   " + '\n'
		out += r" / /     / /\/ /_\// /_/ /  / /\/\ \    " + '\n'
		out += r"/ /___/\/ /_/ /_\\/ __  /  / /   _\ \   " + '\n'
		out += r"\____/\____/\____/\/ /_/   \/    \__/   " + '\n'
		out += r"           /___\/\ /\/__   \            " + '\n'
		out += r"          //  // / \ \ / /\/            " + '\n'
		out += r"         / \_//\ \_/ // /               " + '\n'
		out += r"         \___/  \___/ \/                " + '\n'
		return out

	def __init__(self, root):
		self.root = root

		self.root.set_status_bar_text('Arrow Keys: move | Enter or Space: toggle | Shift+R: reset | Q: quit') # | Shift+D: toggle size

		fake_enter = lambda : self.root._handle_key_presses(py_cui.keys.KEY_ENTER)
		self.root.add_key_command(py_cui.keys.KEY_SPACE, fake_enter)

		self.root.add_key_command(py_cui.keys.KEY_R_UPPER, self.reset)
		# self.root.add_key_command(py_cui.keys.KEY_D_UPPER, self.toggle_deluxe)
		
		self.size = 5

		self.add_sidebar()

		self.game = None
		self.positions = []
		self.init_game()


	def add_sidebar(self):
		"""Add a sidebar for the given window
		"""
		column = self.size
		self.root.add_block_label(self.get_logo(), 0, column, row_span=2, column_span=2).set_color(py_cui.BLACK_ON_WHITE)
		self.scoreboard = self.root.add_block_label('Moves: 0 \nWins: 0\nLow Score: inf', 2, column, column_span=2, center=False)

	def gen_callback(self, r,c):
		"""Create an argless function for each button
		"""
		return (lambda : self.play_tile(r,c))

	def init_game(self):
		"""Create the gameboard - used when initializing or when size changes
		"""
		self.positions = []
		self.game = game.Game(self.size)

		for i in range(self.size):
			row = []
			for j in range(self.size):
				row.append(self.root.add_button('', i, j, command=self.gen_callback(i,j)))
			self.positions.append(row)
		
		self.update_display()

	def update_display(self):
		"""Update the board and scoreboard displays
		"""
		for i in range(self.size):
			for j in range(self.size):
				if(self.game.board[i,j]):
					self.positions[i][j].set_color(py_cui.BLACK_ON_WHITE)
				else:
					self.positions[i][j].set_color(py_cui.WHITE_ON_BLACK)

		self.scoreboard._lines = 'Moves: {}\nWins: {}\nLow Score: {}'.format(self.game.moves, self.game.wins, self.game.lowest_score).splitlines()

	def reset(self):
		"""Reset the game and display
		"""
		self.game.reset()
		self.update_display()

	def play_again(self, response):
		"""Handle the play again prompt
		"""
		if response == 'Play Again':
			self.reset()
		elif response == 'Free Play':
			self.game.freeplay = True
		else:
			exit()

	def play_tile(self, row, col):
		"""Play the tile at the given row, col
		"""
		self.game.toggle(row, col)
		self.update_display()
		if self.game.check_win():
			self.root.show_menu_popup('Congratulations, You Won!', ['Play Again', 'Quit', 'Free Play'], self.play_again, run_command_if_none=True)


	def toggle_deluxe(self): # TODO investigate toggling
		if self.size == 5:
			self.size = 6
			self.root.set_title('Lights Out - DELUXE!')
		else:
			self.size = 5
			self.root.set_title('Lights Out!')

		self.add_sidebar()

		self.init_game()


def main():
    """Program entrypoint, create CUI, wrapper object and start it
    """
    root = py_cui.PyCUI(5,7) # 6,8 for larger game
    root.set_title('Lights Out!')
    root.toggle_unicode_borders()
    lightsout = LightsOutCUI(root)
    print(lightsout.get_logo())
    root.start()


main()