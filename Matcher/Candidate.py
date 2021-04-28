import copy


class Candidate:
    professional_bank = []
    self_attribute_bank = []
    language_bank = []

    def __init__(self, candidate_id, title, skills_set):
        self.candidate_id = candidate_id
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

    def display_candidate(self):
        print("Candidate: ", self.candidate_id, "\n", "job title: ", self.title, "\n", "professional_bank:\n",
              self.professional_bank[:], "\nself_attribute_bank:\n", self.self_attribute_bank[:], "\nlanguage_bank:\n",
              self.language_bank[:])

    def get_skill_set(self):
        ret_set = [self.professional_bank, self.self_attribute_bank, self.language_bank]
        return ret_set

    def get_title(self):
        return self.title

    def get_id(self):
        return self.candidate_id
