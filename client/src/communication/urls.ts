
export const getScenarioURL = (scenarioName: string, agentName: string) => `http://localhost:8080/scenario?name=${scenarioName}&agent=${agentName}`

export const getMessageURL = (scenarioName: string, agentName: string) => `http://localhost:8080/message?name=${scenarioName}&agent=${agentName}`

export const getResetURL = (scenarioName: string) => `http://localhost:8080/reset?name=${scenarioName}`

export const getPauseURL = (scenarioName: string) => `http://localhost:8080/pause?name=${scenarioName}`

export const getResumeURL = (scenarioName: string) => `http://localhost:8080/resume?name=${scenarioName}`
