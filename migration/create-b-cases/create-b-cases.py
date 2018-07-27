import logging

from sqlalchemy import create_engine
from structlog import wrap_logger

from config import Config


logger = wrap_logger(logging.getLogger(__name__))


# Create missing B cases without actionplanid's
def create_b_cases():
    logger.info('Creating missing B cases')
    engine = create_engine(Config.CASE_DB_URI)
    connection = engine.connect()

    with open('sql/create_b_cases.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', ' ')

    trans = connection.begin()
    connection.execute(sql)
    trans.commit()
    logging.info('Successfully created B cases')


# Retrieve the case uuid's and associated collectionexercise uuid's
# for the b cases missing their actionplanid's
def get_cases_without_actionplanids():
    logger.info("Retrieving B cases without actionplanid's")
    engine = create_engine(Config.CASE_DB_URI)
    connection = engine.connect()

    with open('sql/get_cases_without_actionplans.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', ' ')

    trans = connection.begin()
    response = connection.execute(sql)
    trans.commit()
    logging.info("Successfully retrieved B cases without actionplanid's")
    return [row for row in response]


def get_actionplan_id(case_id, collection_exercise_id):
    logger.info('Retrieving actionplan_id for case',
                case_id=case_id, collection_exercise_id=collection_exercise_id)
    engine = create_engine(Config.COLLEX_DB_URI)
    connection = engine.connect()

    with open('sql/get_actionplanid.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', ' ').format(collection_exercise_id)

    trans = connection.begin()
    response = connection.execute(sql)
    trans.commit()
    actionplan_id = next(str(_[0]) for _ in response)
    logger.info('Successfully retrieved actionplan_id',
                case_id=case_id,
                collection_exercise_id=collection_exercise_id,
                actionplan_id=actionplan_id)
    return actionplan_id


def add_actionplan_id(case_id, actionplan_id):
    logger.info('Updating case with actionplan_id',
                case_id=case_id, actionplan_id=actionplan_id)
    engine = create_engine(Config.CASE_DB_URI)
    connection = engine.connect()

    with open('sql/add_actionplanid.sql', 'r') as sqlScriptFile:
        sql = sqlScriptFile.read().replace('\n', ' ').format(actionplan_id, case_id)

    trans = connection.begin()
    connection.execute(sql)
    trans.commit()
    logger.info('Successfully updated case with actionplan_id',
                case_id=case_id, actionplan_id=actionplan_id)


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    create_b_cases()
    created_b_cases = get_cases_without_actionplanids()
    for case in created_b_cases:
        actionplan_id = get_actionplan_id(case.id, case.collectionexerciseid)
        add_actionplan_id(case.id, actionplan_id)
    logger.info('Successfully created new B cases', count=len(created_b_cases))
