import operator
import Candidate
import Skill


class AllCandidates:
    candidate_DB = {}  # main DB including all data base hold Candidate by their id
    prof_skill_DB = {}  # 3 hash tables one for each skill category that holds all id's relevant to each skill
    self_skill_DB = {}
    lang_skill_DB = {}

    def __init__(self):
        # init all hash tables in current skills definition
        for skill in Skill.professional_bank:
            self.prof_skill_DB[skill] = []
        for skill in Skill.self_attribute_bank:
            self.self_skill_DB[skill] = []
        for skill in Skill.language_bank:
            self.lang_skill_DB[skill] = []

    def add_candidate(self, candidate):
        # adding candidate to main DB and his id to relevant hash tables
        candidate_id = candidate.get_id()
        self.candidate_DB[candidate_id] = candidate
        candidate_skill_set = candidate.get_skill_set()
        for skill in candidate_skill_set[0]:
            self.prof_skill_DB[skill].append(candidate_id)
        for skill in candidate_skill_set[1]:
            self.self_skill_DB[skill].append(candidate_id)
        for skill in candidate_skill_set[2]:
            self.lang_skill_DB[skill].append(candidate_id)

    def remove_candidate(self, can_id):
        # removing candidate from all lists in the hash tables and pop it out and return from main DB
        for skill in self.prof_skill_DB:
            if can_id in self.prof_skill_DB[skill]:
                self.prof_skill_DB[skill].remove(can_id)

        for skill in self.self_skill_DB:
            if can_id in self.self_skill_DB[skill]:
                self.self_skill_DB[skill].remove(can_id)

        for skill in self.lang_skill_DB:
            if can_id in self.lang_skill_DB[skill]:
                self.lang_skill_DB[skill].remove(can_id)

        return self.candidate_DB.pop(can_id)

    def candidate_finder(self, job) -> Candidate:
        # crating new dict holding only id's by summing up only relevant candidate with at least one skill and title
        # match, by that I'm saving iteration all over the big DB. it's decided for 3 parts as I decide to split the
        # skills into categories to allow different weight to each category
        # returning the candidate with the biggest score (summing the num of matches * rational weight)
        skills_weights = job.get_weights_set()
        skills_required = job.get_skill_set()
        job_title = job.get_title()
        curr_score_by_id = {}
        for skill in skills_required[0]:
            for key in self.prof_skill_DB[skill]:
                if self.candidate_DB[key].get_title() == job_title:
                    if key in curr_score_by_id:
                        curr_score_by_id[key] += int(skills_weights[0])
                    else:
                        curr_score_by_id[key] = int(skills_weights[0])

        for skill in skills_required[1]:
            for key in self.self_skill_DB[skill]:
                if self.candidate_DB[key].get_title() == job_title:
                    if key in curr_score_by_id:
                        curr_score_by_id[key] += int(skills_weights[1])
                    else:
                        curr_score_by_id[key] = int(skills_weights[1])

        for skill in skills_required[2]:
            for key in self.lang_skill_DB[skill]:
                if self.candidate_DB[key].get_title() == job_title:
                    if key in curr_score_by_id:
                        curr_score_by_id[key] += int(skills_weights[2])
                    else:
                        curr_score_by_id[key] = int(skills_weights[2])

        curr_candidate_id = max(curr_score_by_id.items(), key=operator.itemgetter(1))[0]

        return self.candidate_DB.get(curr_candidate_id)
