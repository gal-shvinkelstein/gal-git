import copy


class Job:
    professional_bank = []
    self_attribute_bank = []
    language_bank = []
    prof_weight = 1
    self_weight = 1
    lan_weight = 1

    def __init__(self, title, skills_set):
        self.title = title
        self.professional_bank = copy.copy(skills_set[0])
        self.self_attribute_bank = copy.copy(skills_set[1])
        self.language_bank = copy.copy(skills_set[2])

    def add_skill(self, skill_type, added_skill):
        if skill_type == "pro":
            self.professional_bank.append(added_skill)
        elif skill_type == "self":
            self.self_attribute_bank.append(added_skill)
        else:
            self.language_bank.append(added_skill)

    def adjust_weights(self, prof_weight, self_weight, lan_weight):
        # to use in case job owner want to impact on final score according to internal considerations
        self.prof_weight = prof_weight
        self.self_weight = self_weight
        self.lan_weight = lan_weight

    def display_job(self):
        print("job title: ", self.title, "\n", "professional_bank:\n",
              self.professional_bank[:], "\nself_attribute_bank:\n", self.self_attribute_bank[:], "\nlanguage_bank:\n",
              self.language_bank[:])

    def get_weights_set(self):
        ret_set = [self.prof_weight, self.self_weight, self.lan_weight]
        return ret_set

    def get_skill_set(self):
        ret_set = [self.professional_bank, self.self_attribute_bank, self.language_bank]
        return ret_set

    def get_title(self):
        return self.title
