import sqlite3, datetime

con = sqlite3.connect("users.db")
cur = con.cursor()

# Reset table:

# cur.execute("DROP TABLE goals")
# cur.execute("CREATE TABLE goals (name, endgoal, startdate, deadline, income_rate, saving_percent)")
# con.commit()
# con.close()

def main():
    goals = [x[0] for x in cur.execute("SELECT name FROM goals").fetchall()]

    class Goal:
        def commit(self):
            cur.execute("INSERT INTO goals VALUES ('{0}', {1}, '{2}', '{3}', {4}, {5})".format(self.name, self.endgoal, self.start_date, self.deadline, self.income_rate, self.saving_percent))
            con.commit()
            con.close()

        def initialize(self):
            self.name = self.get_name()
            self.endgoal = self.get_endgoal()
            self.start_date = datetime.date.today()
            self.deadline = self.get_deadline()
            self.income_rate = self.get_income_rate()
            self.saving_percent = self.get_saving_percent()

        def load(self, name):
            info = cur.execute("SELECT * FROM goals WHERE name='{0}'".format(name)).fetchall()[0]
            self.name = info[0]
            self.endgoal = int(info[1])
            self.start_date = datetime.datetime.strptime(info[2], '%Y-%m-%d').date()
            self.deadline = datetime.datetime.strptime(info[3], '%Y-%m-%d').date()
            self.income_rate = info[4]
            self.saving_percent = info[5]

        def get_name(self):
            name = ""
            while name == "" or name in goals:
                name = input("What would you like to name your goal? ")
            return name

        def get_endgoal(self):
            endgoal = 0
            try:
                while endgoal <= 0:
                    endgoal = float(input("How much money would you like to save in total (in AED)? "))
            except(ValueError):
                return self.get_endgoal()
            return endgoal

        def get_deadline(self):
            try:
                return datetime.datetime.strptime(input("What would you like to set as the deadline for your goal (dd/mm/yyyy)? "), '%d/%m/%Y').date()
            except(ValueError):
                 return self.get_deadline()

        def get_income_rate(self):
            rate = 0
            try:
                while rate <= 0:
                    rate = float(input("How much money do you make a month (in AED)? "))
            except(ValueError):
                return self.get_income_rate()
            return rate

        def get_saving_percent(self):
            rate = -1
            try:
                while rate < 0 or rate > 100:
                    rate = float(input("What percent of money gained do you save (0-100)? "))
            except(ValueError):
                return self.get_income_rate()
            return rate

        def current_savings(self):
            # current_month = datetime.date.today().month
            current_month = datetime.datetime.strptime("1/1/2023", '%d/%m/%Y').date().month
            return self.income_rate * (self.start_date.month - current_month)

        def calculate_percent(self):
            return f"{100 * (self.current_savings() / self.endgoal):.2f}"

    res = cur.execute("SELECT * FROM goals").fetchall()
    if not res:
        print("You appear to not have any goals. Want to start one?")
        goal = Goal()
        goal.initialize()
        goal.commit()
        print(f"You are {goal.calculate_percent()}% of the way there!")
    else:
        print("Welcome back!")

        match input("Would you like to start a new goal? (Y/N) ").upper():
            case 'Y':
                goal = Goal()
                goal.initialize()
                goal.commit()
            case 'YES':
                goal = Goal()
                goal.initialize()
                goal.commit()
            case _:
                goal = Goal()
                while True:
                    try:
                        goal_name = input(f"Which goal would you like to open? ({', '.join(goals)}) ")
                        goal.load(goal_name)
                        break
                    except(IndexError):
                        print(f"Sorry, you do not have any goals named '{goal_name}'. These are your goals:\n{', '.join(goals)}")

        print(f"You are {goal.calculate_percent()}% of the way there!")


if __name__ == '__main__':
    main()