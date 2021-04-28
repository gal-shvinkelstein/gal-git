import Skill
import Candidate
import Job
from Candidate_DB import AllCandidates


def run_auto_test():
    # building base skill set
    prof_set = [Skill.professional_bank[1]]
    self_set = [Skill.self_attribute_bank[1]]
    lan_set = [Skill.language_bank[0], Skill.language_bank[1]]
    skill_set = [prof_set, self_set, lan_set]

    # filling a demo DB with 4 different candidates
    my_candidates = AllCandidates()

    print("\n####### first candidate #######\n")
    new_can1 = Candidate.Candidate("123", "programmer", skill_set)
    new_can1.display_candidate()
    my_candidates.add_candidate(new_can1)
    print("\n###############################\n")

    print("\n####### second candidate #######\n")
    new_can2 = Candidate.Candidate("456", "programmer", skill_set)
    new_can2.add_skill("self", Skill.self_attribute_bank[0])
    new_can2.add_skill("self", Skill.self_attribute_bank[2])
    new_can2.add_skill("lan", Skill.language_bank[3])
    new_can2.display_candidate()
    my_candidates.add_candidate(new_can2)
    print("\n###############################\n")

    print("\n####### third candidate #######\n")
    new_can3 = Candidate.Candidate("789", "product manager", skill_set)
    new_can3.add_skill("pro", Skill.professional_bank[0])
    new_can3.add_skill("pro", Skill.professional_bank[2])
    new_can3.add_skill("self", Skill.self_attribute_bank[2])
    new_can3.display_candidate()
    my_candidates.add_candidate(new_can3)
    print("\n###############################\n")

    print("\n####### forth candidate #######\n")
    new_can4 = Candidate.Candidate("101", "programmer", skill_set)
    new_can4.add_skill("pro", Skill.professional_bank[0])
    new_can4.add_skill("pro", Skill.professional_bank[2])
    new_can4.display_candidate()
    my_candidates.add_candidate(new_can4)
    print("\n###############################\n")

    # creating job description
    print("\n####### job description #######\n")
    curr_job = Job.Job("programmer", skill_set)
    curr_job.add_skill("pro", Skill.professional_bank[0])
    curr_job.add_skill("pro", Skill.professional_bank[2])
    curr_job.add_skill("self", Skill.self_attribute_bank[0])
    curr_job.add_skill("self", Skill.self_attribute_bank[2])
    curr_job.add_skill("lan", Skill.language_bank[3])

    # comment / uncomment the next line to see the difference between the results with or without adjusting the weights
    curr_job.adjust_weights(5, 3, 1)  # professional skills get the biggest weight

    curr_job.display_job()
    print("\n###############################\n")

    print("\n( before calling the finder function ) according to all candidates and job description 101 should be "
          "addressed as the best fit for the job if weights adjusted and 456 if not\n")

    # running the algorithm
    best_candidate = my_candidates.candidate_finder(curr_job)
    print("\nafter calling the the function return: \n")
    best_candidate.display_candidate()


def run_interactive_test():
    status = 1
    print("Instructions:\n1.You are starting with an empty data base first add few candidate\n2.Second step include"
          " 2 phases first defining the job description and then running the matcher algorithm\n3.you can repeat"
          " first and second steps as many time as you want, in order to finish the interactive testing chose "
          "Close aap\n*** In order to have a match you need at least to have one candidate with same title and at "
          "least one match\n*** The weights categories adjustment is by default have same weight, meaning "
          "algorithm threats them equally - candidate with the most matchers will\nbe chosen with no affect on the "
          "categories, if you choose to adjust the weights the algorithm will calculate this in accordance")
    my_candidates = AllCandidates()
    while status:
        user_in = input("\nPlease chose:\n1 - for adding new candidate\n2 - for defining a job description and running"
                        " the matcher\n3 - for closing the app\n")

        if user_in == "1":
            new_candidate_id = input("please enter your candidate id (need do be unique)")
            new_candidate_title = input("please enter your candidate title")

            new_prof_set = []
            new_self_set = []
            new_lan_set = []

            inner_status1 = 1
            inner_status2 = 1
            inner_status3 = 1

            while inner_status1:
                new_prof_skill = input("To add new professional skill please chose number:\n1. C\n2. C++\n3. Java\n4. "
                                       "Python\n5. SQL\n6. Algorithm\n7. To next category\n")

                if new_prof_skill != "7":
                    new_prof_set.append(Skill.professional_bank[int(new_prof_skill) - 1])
                elif new_prof_skill == "7":
                    inner_status1 = 0
                else:
                    print("Wrong input\n")
            while inner_status2:
                new_self_skill = input("To add new self skill please chose number:\n1. Team Player\n2. Autodidact\n3."
                                       " problem solver\n4. To next category\n")

                if new_self_skill != "4":
                    new_self_set.append(Skill.self_attribute_bank[int(new_self_skill) - 1])
                elif new_self_skill == "4":
                    inner_status2 = 0
                else:
                    print("Wrong input\n")

            while inner_status3:
                new_lan_skill = input("To add new language skill please chose number:\n1. English\n2. Hebrew\n3."
                                      " Spanish\n4. French\n5. Russian\n6. To next category\n")

                if new_lan_skill != "6":
                    new_lan_set.append(Skill.language_bank[int(new_lan_skill) - 1])
                elif new_lan_skill == "6":
                    inner_status3 = 0
                else:
                    print("Wrong input\n")

            new_skill_set = [new_prof_set, new_self_set, new_lan_set]
            my_candidates.add_candidate(Candidate.Candidate(new_candidate_id, new_candidate_title, new_skill_set))
            print("Candidate added\n")
        elif user_in == "2":
            print("creating new job description\n")
            new_job_title = input("please enter your job title\n")

            job_prof_set = []
            job_self_set = []
            job_lan_set = []

            inner_status1 = 1
            inner_status2 = 1
            inner_status3 = 1

            while inner_status1:
                new_prof_skill = input("To add new professional skill please chose number:\n1. C\n2. C++\n3. Java\n4. "
                                       "Python\n5. SQL\n6. Algorithm\n7. To next category\n")

                if new_prof_skill != "7":
                    job_prof_set.append(Skill.professional_bank[int(new_prof_skill) - 1])
                elif new_prof_skill == "7":
                    inner_status1 = 0
                else:
                    print("Wrong input\n")
            while inner_status2:
                new_self_skill = input("To add new self skill please chose number:\n1. Team Player\n2. Autodidact\n3."
                                       " problem solver\n4. To next category\n")

                if new_self_skill != "4":
                    job_self_set.append(Skill.self_attribute_bank[int(new_self_skill) - 1])
                elif new_self_skill == "4":
                    inner_status2 = 0
                else:
                    print("Wrong input\n")

            while inner_status3:
                new_lan_skill = input("To add new language skill please chose number:\n1. English\n2. Hebrew\n3."
                                      " Spanish\n4. French\n5. Russian\n6. To next category\n")

                if new_lan_skill != "6":
                    job_lan_set.append(Skill.language_bank[int(new_lan_skill) - 1])
                elif new_lan_skill == "6":
                    inner_status3 = 0
                else:
                    print("Wrong input\n")

            new_job_set = [job_prof_set, job_self_set, job_lan_set]
            new_job = Job.Job(new_job_title, new_job_set)
            adj_input = input("You would like to adjust categories weights? chose y/n (choosing no will leave all"
                              " categories with same weight\n")
            if adj_input == "y" or adj_input == "Y":
                prof_input = input("please enter weight for professional category (num 1-10)\n")
                self_input = input("please enter weight for self skill category (num 1-10)\n")
                lan_input = input("please enter weight for language category (num 1-10)\n")
                new_job.adjust_weights(prof_input, self_input, lan_input)

            best_candidate = my_candidates.candidate_finder(new_job)
            print("\nYour best match is: \n")
            best_candidate.display_candidate()

        elif user_in == "3":
            status = 0
        else:
            print("Wrong input\n")


if __name__ == '__main__':
    valid = 1
    while valid:
        user_input = input("Pleas choose: 1 - for automatic test\n 2 - for interactive test\n ")
        if user_input == "1":
            run_auto_test()
            valid = 0
        elif user_input == "2":
            run_interactive_test()
            valid = 0
        else:
            print("Wrong input\n")
