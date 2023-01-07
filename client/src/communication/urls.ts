
export const getScenarioURL = (scenarioName: string, agentName: string) => `http://localhost:8080/scenario?name=${scenarioName}&agent=${agentName}`

export const getMessageURL = (scenarioName: string, agentName: string) => `http://localhost:8080/message?name=${scenarioName}&agent=${agentName}`
