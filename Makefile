NAME = n-puzzle

all: $(NAME)

$(NAME):
	@mvn package
	@echo "how to run: java -classpath target/classes lu.idk.Main [puzzle] [heuristic]"

re: fclean all

clean:
	rm -rf target

fclean: clear
	rm -f pattern_database_*

.PHONY: all re clean fclean
