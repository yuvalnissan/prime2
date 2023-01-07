
export interface Data {
    id: string,
    type: string,
    value?: string,
    expressions?: Expression[]
}

export interface Expression extends Data {
    negative?: boolean
}
  